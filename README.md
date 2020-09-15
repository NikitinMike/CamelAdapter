# Camel Adapter as is

> mvn install


> mvn camel:run

> curl --location --request POST 'http://localhost:8080/adapter' \
--header 'Content-Type: text/plain' \
--data-raw '{
"msg": "ничего на свете лучше нету",
"lng": "ru",
"coordinates": {
"latitude": "54.35",
"longitude": "52.52"
}
}'

