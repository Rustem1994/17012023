package org.rustem.application.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rustem.application.dto.TaskDto;
import org.rustem.application.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@EmbeddedKafka
@Slf4j
public class TaskKafkaServiceTest {

    @Value("${spring.kafka.producer.topic}")
    private String topics;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private final ObjectMapper mapper = new ObjectMapper();

    private BlockingQueue<ConsumerRecord<String, String>> records;
    private KafkaMessageListenerContainer<String, String> container;
    @Autowired
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        log.info("setUp");
        Map<String, Object> configs = new HashMap<>(
                KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        DefaultKafkaConsumerFactory<String, String> consumerFactory
                = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new StringDeserializer());
        container = new KafkaMessageListenerContainer<>(consumerFactory, new ContainerProperties(topics));
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    void tearDown() {
        log.info("tearDown");
        container.stop();
    }

    @Test
    public void send() {
        //Подготовка
        TaskDto dto = TaskDto.builder().name("TEST").status("ON").word("rrrrrrr").build();

        // Выполнение
        taskService.send(dto);
        ConsumerRecord<String, String> singleRecord = null;
        try {
            singleRecord = records.poll(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Проверки
        assertThat(singleRecord).isNotNull();

    }
}

