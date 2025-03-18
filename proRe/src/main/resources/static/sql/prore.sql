-- 시퀀스 생성 (각 테이블마다 시퀀스를 생성해야 함)
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE movies_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE reviews_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE likes_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE bookmarks_seq START WITH 1 INCREMENT BY 1 NOCACHE;

-- 트리거 생성 (각 테이블마다 트리거 생성)
-- 사용자 테이블 (users)
CREATE OR REPLACE TRIGGER users_before_insert
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    SELECT users_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

-- 영화 테이블 (movies)
CREATE OR REPLACE TRIGGER movies_before_insert
BEFORE INSERT ON movies
FOR EACH ROW
BEGIN
    SELECT movies_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

-- 리뷰 테이블 (reviews)
CREATE OR REPLACE TRIGGER reviews_before_insert
BEFORE INSERT ON reviews
FOR EACH ROW
BEGIN
    SELECT reviews_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

-- 좋아요 테이블 (likes)
CREATE OR REPLACE TRIGGER likes_before_insert
BEFORE INSERT ON likes
FOR EACH ROW
BEGIN
    SELECT likes_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

-- 북마크 테이블 (bookmarks)
CREATE OR REPLACE TRIGGER bookmarks_before_insert
BEFORE INSERT ON bookmarks
FOR EACH ROW
BEGIN
    SELECT bookmarks_seq.NEXTVAL INTO :NEW.id FROM dual;
END;
/

-- 사용자 테이블 생성
CREATE TABLE users (
    id NUMBER PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    email VARCHAR2(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 영화 테이블 생성
CREATE TABLE movies (
    id NUMBER PRIMARY KEY,
    title VARCHAR2(255) NOT NULL,
    director VARCHAR2(100),
    release_date DATE,
    genre VARCHAR2(50),
    poster_image_name VARCHAR2(255),
    synopsis CLOB,  -- 줄거리 요약
    average_rating NUMBER DEFAULT 0  -- 영화의 평균 평점
);

-- 리뷰 테이블 생성
CREATE TABLE reviews (
    id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    movie_id NUMBER NOT NULL,
    rating NUMBER CHECK (rating BETWEEN 1 AND 5),  -- 평점 (1~5점)
    review_comment CLOB,  -- 리뷰 내용
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- 좋아요 테이블 생성
CREATE TABLE likes (
    id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    review_id NUMBER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_review UNIQUE (user_id, review_id)  -- 한 사용자는 같은 리뷰에 한 번만 좋아요 가능
);

-- 북마크 테이블 생성
CREATE TABLE bookmarks (
    id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    movie_id NUMBER NOT NULL,  -- 영화 북마크
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_movie UNIQUE (user_id, movie_id)  -- 중복 저장 x
);