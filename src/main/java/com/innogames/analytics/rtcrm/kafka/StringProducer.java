package com.innogames.analytics.rtcrm.kafka;

import java.util.Properties;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class StringProducer {

	private final Properties properties;
	private Producer<String, String> producer;

	public StringProducer(final Properties properties) {
		this.properties = new Properties();

		// default settings
		this.properties.put("bootstrap.servers", "localhost:9092");
		this.properties.put("acks", "all");
		this.properties.put("buffer.memory", 33554432);
		this.properties.put("compression.type", "snappy");
		this.properties.put("retries", 3);
		this.properties.put("batch.size", 16384);
		this.properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		this.properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		// custom settings
		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

	public void start() {
		this.producer = new KafkaProducer<>(this.properties);
	}

	public void stop() {
		this.flush();
		this.producer.close();
	}

	public void flush() {
		this.producer.flush();
	}

	public Future<RecordMetadata> send(ProducerRecord<String, String> record) {
		return this.producer.send(record);
	}

	public Future<RecordMetadata> send(String topic, String message) {
		return this.send(new ProducerRecord<>(topic, message));
	}

}
