package org.rustem.application.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rustem.application.dto.TaskDto;
import org.rustem.application.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApiTask {

    @Autowired
    private TaskService taskService;

    @PostMapping(path = "${api.task.find}")
    public String setGroup(@RequestBody TaskDto taskDto) {
        log.info("Task {}",taskDto);
      //  taskService.find(taskDto);
     //   taskService.findmaxword(taskDto);
        return taskService.find(taskDto).toString();
    }
}
