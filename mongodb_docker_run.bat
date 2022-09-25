docker run -it --rm --name mongo-report-integrator^
 -p 27017:27017 -v C:/fileserver/data/report-integrator/db:/data/db^
 -e MONGO_INITDB_ROOT_USERNAME=mongoadmin^
 -e MONGO_INITDB_ROOT_PASSWORD=2v4NdRGvGDZbfrkE^
 mongo