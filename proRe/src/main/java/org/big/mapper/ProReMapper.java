package org.big.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.big.dto.MovieDto;

@Mapper
public interface ProReMapper {
    List<MovieDto> getAllMovies();
    MovieDto findById(Long id);
}
