package org.rustem.application.mapper;

import org.mapstruct.Mapper;
import org.rustem.application.dto.TaskDto;
import org.rustem.application.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    Task fromDto(TaskDto source);
}
