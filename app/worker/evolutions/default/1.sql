# --- Account and Email

# --- !Ups

CREATE TABLE "User" (
  "id"       SERIAL PRIMARY KEY,
  "username" VARCHAR(255) NOT NULL
);

CREATE TABLE "UserData" (
  "id"     SERIAL PRIMARY KEY,
  "userId" BIGINT REFERENCES "User",
  "followers"  VARCHAR(255) NOT NULL
);

CREATE TABLE "AccountTokens" (
  "id"        SERIAL PRIMARY KEY,
  "userId"    BIGINT REFERENCES "Account",
  "provider"  VARCHAR(255) NOT NULL,
  "token"     TEXT         NOT NULL,
  "timestamp" TIMESTAMP    NOT NULL DEFAULT NOW()
);
