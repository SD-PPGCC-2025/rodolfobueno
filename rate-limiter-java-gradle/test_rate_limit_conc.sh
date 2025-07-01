#!/bin/bash

# Clients with different API keys
clients=("alice" "bob" "charlie")
total_requests=40
concurrent_requests=10  # Number of concurrent requests per client

echo "Sending $total_requests requests per client with $concurrent_requests concurrent requests..."

# Create a temporary file to store results
result_file=$(mktemp)

# Function to make requests for a specific client
make_client_requests() {
    local client=$1
    echo -e "\nStarting requests for client: $client"
    
    # Function to make a single request
    make_request() {
        local request_num=$1
        local timestamp=$(date +"%H:%M:%S")
        local response=$(curl -s -w "HTTP-%{http_code}" -H "api-key: $client" "http://localhost:8080/api/check")
        echo "[$timestamp] [$client] Request $request_num: $response" >> "$result_file"
    }
    
    # Make requests in batches of concurrent_requests
    for ((i=1; i<=total_requests; i+=concurrent_requests)); do
        # Start concurrent requests
        for ((j=0; j<concurrent_requests && (i+j)<=total_requests; j++)); do
            make_request $((i+j)) &
        done
        # Wait for all background processes to complete
        wait
    done
}

# Start requests for all clients concurrently
for client in "${clients[@]}"; do
    make_client_requests "$client" &
done

# Wait for all client processes to complete
wait

# Display results
echo -e "\nAll requests completed. Results:"
cat "$result_file"
rm "$result_file"
