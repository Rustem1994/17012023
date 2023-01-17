package org.rustem.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.rustem.application.dto.TaskDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    @Value("${folder}")
    private String folder;

    private final ObjectMapper objectMapper;
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${url}")
    private String url;
    @Value("${api.task.find}")
    private String url_api;

    @Value("${spring.kafka.producer.topic}")
    private String topics;

    private final KafkaTemplate<Long, TaskDto> kafkaTemplate;



    public TaskDto find(TaskDto dto){
        log.info("Начало");
        if (dto.getWord()!=null){
            List<File> lst = Arrays.stream((new File(folder)).listFiles()).toList();
            log.info("Список файлов в папке folder = "+lst.toString());
            lst.stream().filter(File::isFile).forEach(file -> {
                log.info("Файл "+file.getName());
                try {
                    int count_find_word= Files.lines(Paths.get(file.getAbsolutePath())).filter(line -> line.contains(dto.getWord())).toList().size();
                    log.info("Количество совпадении в файле "+count_find_word);
                    log.info("Текущее значение количество совпадении "+dto.getCount_find_word());
                    if (count_find_word>dto.getCount_find_word()){
                        dto.setCount_find_word(count_find_word);
                        dto.setFile_name(file.getName());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }else{
            log.info("Word пустой");
        }
        log.info("Task {}",dto);
        log.info("Конец");
        return dto;

    }

    public void send(TaskDto dto) {
        log.info("Начало");
      //  TaskDto dto = TaskDto.builder().name("TEST").status("ON").word("rrrrrrr").build();
        ListenableFuture<SendResult<Long, TaskDto>> future = kafkaTemplate.send(topics, dto);
        future.addCallback(result -> log.info("Сообщение отправлено {},offset {}", dto,
                        result != null ? result.getRecordMetadata().offset() : "отсутсвтует"),
                ex -> log.error("Прноизошла ошибка {}", ex.getMessage())

        );
        log.info("Task {}",dto);
        log.info("Конец");
    }

    @KafkaListener(id = "service", topics = {"${spring.kafka.producer.topic}"})
    public void consume(ConsumerRecord<String, String> req) {
        log.info("Начало");
        TaskDto dto = null;
        try {
            dto = objectMapper.readValue(req.value(), TaskDto.class);
            log.info("Получили {}", dto);
            dto = restTemplate.postForObject(url + url_api, dto, TaskDto.class);

        } catch (JsonProcessingException ex) {
            log.error("Ошибка десериализции: " + ex.getMessage() + " object:" + req.value());
        }
        log.info("Task {}",dto);
        log.info("Конец");

    }

}
