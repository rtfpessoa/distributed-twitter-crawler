# --- !Ups

CREATE TABLE "User" (
  "id"       SERIAL PRIMARY KEY,
  "username" VARCHAR(255) NOT NULL
);

CREATE TABLE "UserData" (
  "id"        SERIAL PRIMARY KEY,
  "userId"    BIGINT REFERENCES "User" NOT NULL,
  "followers" INT                      NOT NULL,
  "following" INT                      NOT NULL
);

CREATE TABLE "UserTweet" (
  "id"     SERIAL PRIMARY KEY,
  "userId" BIGINT REFERENCES "User" NOT NULL,
  "tweet"  VARCHAR(255)             NOT NULL
);

CREATE TABLE "Worker" (
  "id" SERIAL PRIMARY KEY,
  "ip" VARCHAR(255) NOT NULL
);

CREATE TABLE "Work" (
  "id"       SERIAL PRIMARY KEY,
  "workType" VARCHAR(255)             NOT NULL,
  "workerId" BIGINT REFERENCES "Worker",
  "userId"   BIGINT REFERENCES "User" NOT NULL,
  "state"    VARCHAR(255)             NOT NULL,
  "offset"   INT
);

CREATE TABLE "APILimit" (
  "id"          SERIAL PRIMARY KEY,
  "endpoint"    VARCHAR(255) NOT NULL,
  "windowStart" TIMESTAMP    NOT NULL,
  "requests"    INT          NOT NULL DEFAULT 0
);

-- initial users

INSERT INTO "User" ("username") VALUES ('rtfpessoa');

# --- !Downs

DROP TABLE IF EXISTS "User" CASCADE;
DROP TABLE IF EXISTS "UserData" CASCADE;
DROP TABLE IF EXISTS "UserTweet" CASCADE;
DROP TABLE IF EXISTS "Work" CASCADE;
DROP TABLE IF EXISTS "Worker" CASCADE;
