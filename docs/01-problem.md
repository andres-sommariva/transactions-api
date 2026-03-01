# transactions-api - Problem

Details about the problem to be solved.

## What to build

- Build a RESTfull webservice that stores transactions (in memory) and returns information about those transactions.
- Transactions contain a 'type' and an 'amount'.
- The service should be able to return transactions by 'type'.
- Transactions can be linked to a parent transaction using a 'parent_id'.
- The service should be able to return the total amount involved for a transaction and its children transactions.

### MUST have

- Integration tests
- Dockerized application
- Java 11 or higher
- Readable code
- Correct architectural design

### NICE to have

- TDD
- Incremental development and progressive commits
- SOLID design principles
- Documentation

## Required API specification

### `PUT /transactions/$transaction_id`

Body:

```json
{
  "amount": double,
  "type": string,
  "parent_id": long
}
```

Where:
- **transaction_id**: `long`, is the new transaction identifier
- **amount**: `double`, is the transaction amount. 
- **type**: `string`, is the transaction type. 
- **parent_id**: (optional) `long`, is the parent transaction id.

### `GET /transactions/types/$type`

Returns:

```json
[
  long,
  long,
  ...
]
```

Returns a list of all transaction ids for the given type.

### `GET /transactions/sum/$transaction_id`

Returns:

```json
{
  "sum": double
}
```

Returns the total amount of the specified transaction plus its children linked through their `parent_id`.