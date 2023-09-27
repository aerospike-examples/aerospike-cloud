package main

import (
	"crypto/tls"
	"log"
	"os"
	"time"

	"github.com/aerospike/aerospike-client-go/v6"
)

func main() {
	// ***
	// Setup
	// ***

	address := os.Getenv("AEROSPIKE_CLOUD_HOSTNAME")            // Aerospike Cloud cluster address
	port := 4000                                                // Aerospike Cloud cluster port
	host := aerospike.NewHost(address, port)                    // Create host
	apiKeyId := os.Getenv("AEROSPIKE_CLOUD_API_KEY_ID")         // API Key ID from Aerospike Cloud account
	apiKeySecret := os.Getenv("AEROSPIKE_CLOUD_API_KEY_SECRET") // API Key secret from Aerospike Cloud account
	namespace := "aerospike_cloud"                              // Cluster namespace
	set := "foo"                                                // Set name within namespace

	// Create a ClientPolicy passing in your API credentials
	// and setting up TLS (required for Aerospike Cloud)
	clientPolicy := aerospike.NewClientPolicy()
	clientPolicy.User = apiKeyId
	clientPolicy.Password = apiKeySecret
	clientPolicy.TlsConfig = &tls.Config{}

	// Create the client and connect to the database
	client, err := aerospike.NewProxyClientWithPolicyAndHost(clientPolicy, host)
	if err != nil {
		log.Fatal(err)
	}
	defer client.Close()

	// ***
	// Write a record
	// ***

	// Create a WritePolicy to set the TotalTimeout for writes
	// default 1000 ms
	writePolicy := aerospike.NewWritePolicy(0, 0)
	writePolicy.TotalTimeout = 5000 * time.Millisecond

	// Create the record key
	// A tuple consisting of namespace, set name, and user defined key
	key, err := aerospike.NewKey(namespace, set, "bar")
	if err != nil {
		log.Fatal(err)
	}

	// Create a bin to store data within the new record
	bin := aerospike.NewBin("myBin", "Hello World!")

	//Write the record to your database
	err = client.PutBins(writePolicy, key, bin)
	if err != nil {
		log.Fatal(err)
	}
	log.Println("Succesfully wrote record")

	// ***
	// Read back the record we just wrote
	// ***

	// Create a Policy to set the TotalTimeout for reads
	// default 1000 ms
	readPolicy := aerospike.NewPolicy()
	readPolicy.TotalTimeout = 5000 * time.Millisecond

	// Read the record
	record, err := client.Get(readPolicy, key)
	if err != nil {
		log.Fatal(err)
	}

	log.Printf("Record: %s", record.Bins)
}
