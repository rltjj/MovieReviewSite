package org.big.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.big.dto.BookmarkDto;
import java.util.List;

@Mapper
public interface BookmarkMapper {
    void insertBookmark(BookmarkDto bookmark);
    List<BookmarkDto> findBookmarksByUser(Long userId);
    void deleteBookmark(Long userId, Long movieId);
    Integer checkBookmark(@Param("userId") Long userId, @Param("movieId") Long movieId);
    int isBookmarked(Long userId, Long movieId);
}
