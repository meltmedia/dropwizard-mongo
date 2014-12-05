/**
 * Copyright (C) 2014 meltmedia (christian.trimble@meltmedia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meltmedia.dropwizard.mongo.example.resources;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.DB;

@Path("/")
public class RootResource {

  DB database;
  Jongo jongo;

  public RootResource(DB database) {
    this.database = database;
    this.jongo = new Jongo(database);
  }
  
  @GET
  @Produces("application/json")
  public Set<String> collectionNames() {
    return database.getCollectionNames();
  }
  
  @Path("{collectionName}")
  public CollectionResource collection( @PathParam("collectionName") String name ) {
    return new CollectionResource(jongo.getCollection(name));
  }
  
  public class CollectionResource {
    
    MongoCollection collection;

    public CollectionResource(MongoCollection collection) {
      this.collection = collection;
    }
    
    @GET
    @Produces("application/json")
    public List<String> list() {
      return collection.distinct("_id").as(String.class);
    }
    
    @DELETE
    public void delete() {
      collection.remove(); 
    }
    
    @GET
    @Path("{id}")
    @Produces("application/json")
    public ObjectNode getDocument( @PathParam("id") String id ) {
      ObjectNode node = collection.findOne("{_id: #}", id).as(ObjectNode.class);
      if( node == null ) {
        throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
      }
      return node;
    }
    
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response putDocument( @Context UriInfo uriInfo, Map<String, JsonNode> document ) {
      ObjectId id = new ObjectId();
      document.put("_id", JsonNodeFactory.instance.textNode(id.toString()));
      collection.save(document);
      return Response
          .created(uriInfo
              .getAbsolutePathBuilder()
              .path(id.toString())
              .build())
          .header("X-Document-ID", id.toString())
          .build();
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response putDocument( @PathParam("id") String id, Map<String, JsonNode> document ) {
      document.put("_id", JsonNodeFactory.instance.textNode(id));
      collection.save(document);
      return Response.noContent().build();
    }
    
    @DELETE
    @Path("{id}")
    @Produces("application/json")
    public Response deleteDocument(  @PathParam("id") String id ) {
      collection.remove("{_id: #}", id);
      return Response.noContent().build();
    }
  }

}
