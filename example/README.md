# Dropwizard Mongo Example

This example shows how to add mongo configuration to your Dropwizard project.  The example application lets you do basic CRUD operations on the documents stored in Mongo.

## Before You Begin

- Make sure you have build the project using the instructions in the parent project.
- These instructions assume that the example project is your current working directory.

## Starting the Server

The mongo password in the example is encrypted, so you will need to set the encryption passphrase in your environment
before starting the server.

```
export EXAMPLE_PASSPHRASE='correct horse battery staple`
```

Also, we will need to start an instance of mongo to use the application with.  There is a vagrant file in the root
directory that will start a VM, install mongo and add our example user.

```
vagrant up
```

Once vagrant has started, we can start the server.

```
./target/dropwizard-mongo server conf/example.yml
```

That is it, the example application should be running.

## Health Check

To make sure that the example is really working, you can curl the health resource on the admin port.

```
curl http://localhost:8081/healthcheck
```

You should see output like this if everything started properly.

```
{"deadlocks":{"healthy":true},"mongo":{"healthy":true}}
```

## API Root

### GET

Getting the root of the application will get you a list of the collections.

```
curl http://localhost:8080/
```

```
[
  "name1",
  "name2"
]
```

## Collections

### GET

Getting a collection will list the ids.

```
curl http://localhost:8080/col
```

```
[
  "id1",
  "id2"
]
```

### POST

Posting to a collection will insert a document.

```
curl -X POST -H "Content-Type: application/json" http://localhost:8080/col \
-d '{"field", "value"}'
```

and return the documents id and location.

### DELETE

Deleting the collection will remove it from Mongo.

```
curl -X DELETE http://localhost:8080/col
```

## Documents

### GET

Getting a document's ID from a collection will return it.

```
curl http://localhost:8080/col/{id}
```

### PUT

Putting a document to an ID in a collection will upsert the document.

```
curl -X PUT -H "Content-Type: application/json" http://localhost:8080/col/{id} \
-d '{"field", "new value"}'
```

### DELETE

Deleting a document's ID from a collection will delete it.

```
curl -X DELETE http://localhost:8080/col/{id}
```
