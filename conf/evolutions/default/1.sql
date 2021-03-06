# --- !Ups

CREATE TABLE "User" (
  "id"         SERIAL PRIMARY KEY,
  "username"   VARCHAR(255) NOT NULL UNIQUE,
  "timestamp"  TIMESTAMP    NOT NULL DEFAULT NOW(),
  "lastUpdate" TIMESTAMP    NOT NULL DEFAULT '1980-01-01 00:00:00.097000',
  "nrUpdates"  BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE "UserData" (
  "id"        SERIAL PRIMARY KEY,
  "userId"    BIGINT REFERENCES "User" NOT NULL,
  "followers" BIGINT                   NOT NULL,
  "friends"   BIGINT                   NOT NULL
);

CREATE TABLE "UserTweet" (
  "id"        SERIAL PRIMARY KEY,
  "twitterId" BIGSERIAL                NOT NULL,
  "userId"    BIGINT REFERENCES "User" NOT NULL,
  "tweet"     TEXT                     NOT NULL,
  "timestamp" TIMESTAMP                NOT NULL DEFAULT NOW()
);

CREATE TABLE "Worker" (
  "id"        SERIAL PRIMARY KEY,
  "ip"        VARCHAR(255) NOT NULL,
  "heartbeat" TIMESTAMP    NOT NULL
);

CREATE TABLE "Work" (
  "id"       SERIAL PRIMARY KEY,
  "workType" VARCHAR(255)             NOT NULL,
  "workerId" BIGINT REFERENCES "Worker" ON DELETE SET NULL,
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

CREATE TABLE "Hashtag" (
  "id"      SERIAL PRIMARY KEY,
  "tweetId" BIGINT REFERENCES "UserTweet" ON DELETE SET NULL,
  "label"   VARCHAR(255) NOT NULL
);

CREATE TABLE "Location" (
  "id"      SERIAL PRIMARY KEY,
  "tweetId" BIGINT REFERENCES "UserTweet" ON DELETE SET NULL,
  "label"   VARCHAR(255) NOT NULL
);

-- initial users

INSERT INTO "User" ("username") VALUES ('cnn');

# --- !Downs

DROP TABLE IF EXISTS "Location" CASCADE;
DROP TABLE IF EXISTS "Hashtag" CASCADE;
DROP TABLE IF EXISTS "UserTweet" CASCADE;
DROP TABLE IF EXISTS "UserData" CASCADE;
DROP TABLE IF EXISTS "User" CASCADE;
DROP TABLE IF EXISTS "Work" CASCADE;
DROP TABLE IF EXISTS "Worker" CASCADE;
DROP TABLE IF EXISTS "APILimit" CASCADE;
