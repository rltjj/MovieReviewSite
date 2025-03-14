package org.big.service;

import lombok.RequiredArgsConstructor;
import org.big.dto.BookmarkDto;
import org.big.mapper.BookmarkMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkMapper bookmarkMapper;

    @Override
    public void addBookmark(Long userId, Long movieId) {
        BookmarkDto bookmark = new BookmarkDto();
        bookmark.setUserId(userId);  // 회원 ID 저장
        bookmark.setMovieId(movieId);
        bookmarkMapper.insertBookmark(bookmark);
    }

    @Override
    public List<BookmarkDto> getUserBookmarks(Long userId) {
        return bookmarkMapper.findBookmarksByUser(userId);
    }

    @Override
    public void removeBookmark(Long userId, Long movieId) {
        bookmarkMapper.deleteBookmark(userId, movieId);
    }

    @Override
    public boolean isBookmarked(Long userId, Long movieId) {
        int count = bookmarkMapper.isBookmarked(userId, movieId);  // 북마크 여부 확인
        return count > 0;  // 0보다 크면 북마크된 상태
    }
}
