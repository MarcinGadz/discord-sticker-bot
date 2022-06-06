# discord-sticker-bot
Simple discord bot made from two parts:
  - private Spring Boot API: Used to manage stickers (small PNG images) on the AWS S3 Object Storage.
  It has also mode to run locally, using in-memory java List instead of AWS storage. To do it, you can use command `mvn spring-boot:run -Dspring.boot.run.profiles=LOCAL`.
  After that, it exposes a few endpoints on `http://localhost:8080`:
    - GET on `/{userId}` path - returns all images owned by specified user
    - GET on `/{userId}/{sticker}` path - returns example URL and some metadata about sticker specified by owner and name, or 404 code if sticker does not exist
    - POST on `/{userId}/{sticker}` path - adds sticker passed as MultiPart file, or returns proper response code when something went wrong (like user limit was exceeded)
    - DELETE on `/{userId}/{sticker}` path - removes specified sticker or returns 404 when such sticker does not exist  
    
    To locally run tests of API (both integrational and unit) use `mvn test` command.
    Both commands are supposed to be run from API root directory (`/api` in this repo)
