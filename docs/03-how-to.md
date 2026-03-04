# transactions-api - How To Guides

How to run and test.

## How To

## Run

```
cd transactions-api-impl
mvn spring-boot:run
```

## Test

Unit tests
```
mvn test
```

Unit + Integration tests
```
mvn verify
```

## Use

### 1. Create a transaction

```
PUT /transactions/$transaction_id

Body:
{
    "amount": <double>,
    "type": "<string>",
    "parent_id": <long>
}
```

### 2. Get transactions by type

```
GET /transactions/types/$type

Returns:
[
  <long>,
  <long>,
  ...
  <long>
]
```

### 3. Get transaction total amount

```
GET /transactions/sum/$transaction_id

Returns:
{
  "sum": <double>
}
```

## Run with Docker

### Build the Docker image

```bash
/transactions-api$ sudo docker build -t spring-boot-app:latest .
```

### Run the Docker container

```bash
/transactions-api$ sudo docker run -p 8080:8080 spring-boot-app:latest
```