# Dropwizard Mongo Example

This example shows how to add mongo configuration to your Dropwizard project.

## Before You Begin

- Make sure you have build the project using the instructions in the parent project.
- These instructions assume that the example project is your current working directory.

## Starting the Server

The mongo password in the example is encrypted, so you will need to set the encryption passphrase in your environment
before starting the server.

```
export DROPWIZARD_PASSPHRASE='correct horse battery staple`
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
