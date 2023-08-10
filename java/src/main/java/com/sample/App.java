package com.sample;

import com.aerospike.client.IAerospikeClient;
import com.aerospike.client.proxy.AerospikeClientProxy;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.TlsPolicy;

public class App 
{
    public static void main( String[] args )
    {   
	// ***
        // Setup
        // ***

        String address = System.getenv("AEROSPIKE_CLOUD_HOSTNAME");     		// Aerospike Cloud cluster address
        Integer port = 4000; 			                		// Aerospike Cloud cluster port
        String apiKeyId = System.getenv("AEROSPIKE_CLOUD_API_KEY_ID");         	// API Key ID from Aerospike Cloud account
        String apiKeySecret = System.getenv("AEROSPIKE_CLOUD_API_KEY_SECRET"); 	// API Key secret from Aerospike Cloud account
        String namespace = "aerospike_cloud"; 	                		// Cluster namespace
        String set = "foo"; 			                		// Set name within namespace

        // Create a ClientPolicy passing in your API credentials
	// and setting up TLS (required for Aerospike Cloud)
        TlsPolicy tlsPolicy = new TlsPolicy();
        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.user = apiKeyId;
        clientPolicy.password = apiKeySecret;
        clientPolicy.tlsPolicy = tlsPolicy;

        // Set the totalTimeout default for reads and writes
        // default 1000 ms
        clientPolicy.readPolicyDefault.totalTimeout = 5000;
        clientPolicy.writePolicyDefault.totalTimeout = 5000;

        // Create the client and connect to the database
        IAerospikeClient client = new AerospikeClientProxy(clientPolicy, new Host(address, port));

        // ***
	// Write a record
	// ***

        // Create the record key
	// A tuple consisting of namespace, set name, and user defined key
        Key key = new Key(namespace, set, "bar");

        // Create a bin to store data within the new record
        Bin bin = new Bin("myBin", "Hello World!");

        //Write the record to your database
        try {
            client.put(null, key, bin);
            System.out.println("Successfully wrote record");
        }
        catch (AerospikeException e) {
            e.printStackTrace();
        }

        // ***
	// Read back the record we just wrote
	// ***

        // Read the record
        try {
            Record record = client.get(null, key);
            System.out.format("Record: %s", record.bins);
        }
        catch (AerospikeException e) {
            e.printStackTrace();
        }
        finally {
            client.close();
        }
    }
}
