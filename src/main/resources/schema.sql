/*
quiz : id, 問題文
quizOptions : id(quizと共用)，選択肢(1~4) ，正解番号
ユーザー：ユーザー名、得点、アクティブ
*/

CREATE TABLE users (
    id IDENTITY,
    userName VARCHAR NOT NULL,
    point INT,
    isActive BOOLEAN NOT NULL
);

CREATE TABLE quizzes (
    id IDENTITY,
    question VARCHAR NOT NULL
);

CREATE TABLE quizOptions (
    id IDENTITY,
    choice_1 VARCHAR NOT NULL,
    choice_2 VARCHAR NOT NULL,
    choice_3 VARCHAR NOT NULL,
    choice_4 VARCHAR NOT NULL,
    correct INT NOT NULL
);
