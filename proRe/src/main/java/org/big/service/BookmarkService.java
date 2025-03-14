package org.big.service;

import org.big.dto.BookmarkDto;
import java.util.List;

public interface BookmarkService {

    List<BookmarkDto> getUserBookmarks(Long userId);
    
    void addBookmark(Long userId, Long movieId);
    void removeBookmark(Long userId, Long movieId);
    boolean isBookmarked(Long userId, Long movieId);
}
