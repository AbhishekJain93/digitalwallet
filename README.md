# digitalwallet
REST API for digital-wallet using spring-boot <br> This service supports
1. List all transactions for a wallet
2. Debit a wallet for an amount
3. Credit a wallet for an amount 

## Currency Structure
>The service defines Transaction Amount as a tuple of ***long amount*** and ***String currencyCode (ISO-4217)*** <br>
The currency amount is to be in lowest denomination for that currency code.


## List Transactions for a wallet
> curl --location 'localhost:8080/api/wallets/100/transactions?page=0&size=100'

## Debits
> curl --location 'localhost:8080/api/wallets/1001/debits' \
--header 'Content-Type: application/json' \
--data '{
    "amount" : "100",
    "currencyCode": "GBP",
    "transactionRef": "uniq-ref-1"
}'

## Credits
> curl --location 'localhost:8080/api/wallets/1001/credits' \
--header 'Content-Type: application/json' \
--data '{
    "amount" : "1000",
    "currencyCode": "GBP",
    "transactionRef": "uniq-ref-1"
}'

## To run parallel curl requests
> xargs -P 10 -n 1 curl --location 'localhost:8080/api/wallets/100/debits' --header 'Content-Type: application/json' --data < req.txt