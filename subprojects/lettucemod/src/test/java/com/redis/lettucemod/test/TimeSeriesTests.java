package com.redis.lettucemod.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;

import com.redis.lettucemod.api.sync.RedisTimeSeriesCommands;
import com.redis.lettucemod.timeseries.Aggregation;
import com.redis.lettucemod.timeseries.CreateOptions;
import com.redis.lettucemod.timeseries.RangeOptions;
import com.redis.lettucemod.timeseries.RangeResult;
import com.redis.lettucemod.timeseries.Sample;
import com.redis.testcontainers.junit.jupiter.RedisTestContext;
import com.redis.testcontainers.junit.jupiter.RedisTestContextsSource;

class TimeSeriesTests extends AbstractLettuceModTestBase {

	private static final String LABEL_SENSOR_ID = "sensor_id";
	private static final String LABEL_AREA_ID = "area_id";
	private static final long TIMESTAMP_1 = 1548149181;
	private static final long TIMESTAMP_2 = 1548149191;
	private static final double VALUE_1 = 30;
	private static final double VALUE_2 = 42;
	private static final String KEY = "temperature:3:11";
	private static final String SENSOR_ID = "2";
	private static final String AREA_ID = "32";

	@ParameterizedTest
	@RedisTestContextsSource
	void create(RedisTestContext context) {
		// temperature:3:11 RETENTION 6000 LABELS sensor_id 2 area_id 32
		String status = context.sync().create(KEY, CreateOptions.<String, String>builder().retentionTime(6000).build());
		Assertions.assertEquals("OK", status);
	}

	@ParameterizedTest
	@RedisTestContextsSource
	void add(RedisTestContext context) {
		RedisTimeSeriesCommands<String, String> ts = context.sync();
		// TS.CREATE temperature:3:11 RETENTION 6000 LABELS sensor_id 2 area_id 32
		ts.create(KEY, createOptions());
		// TS.ADD temperature:3:11 1548149181 30
		Long add1 = ts.add(KEY, TIMESTAMP_1, VALUE_1);
		Assertions.assertEquals(TIMESTAMP_1, add1);
		// TS.ADD temperature:3:11 1548149191 42
		Long add2 = ts.add(KEY, TIMESTAMP_2, VALUE_2);
		Assertions.assertEquals(TIMESTAMP_2, add2);
	}

	private CreateOptions<String, String> createOptions() {
		return CreateOptions.<String, String>builder().retentionTime(6000).label(LABEL_SENSOR_ID, SENSOR_ID)
				.label(LABEL_AREA_ID, AREA_ID).build();
	}

	@ParameterizedTest
	@RedisTestContextsSource
	void range(RedisTestContext context) {
		RedisTimeSeriesCommands<String, String> ts = context.sync();
		String key = populateTimeSeries(ts);
		List<Sample> range = ts.range(key, RangeOptions.from(1548149180).to(1548149210)
				.aggregation(Aggregation.of(Aggregation.Type.AVG, 5)).build());
		Assertions.assertEquals(2, range.size());
		Assertions.assertEquals(1548149180, range.get(0).getTimestamp());
		Assertions.assertEquals(VALUE_1, range.get(0).getValue());
		Assertions.assertEquals(1548149190, range.get(1).getTimestamp());
		Assertions.assertEquals(VALUE_2, range.get(1).getValue());
	}

	private String populateTimeSeries(RedisTimeSeriesCommands<String, String> ts) {
		String key = "temperature:3:11";
		// TS.CREATE temperature:3:11 RETENTION 6000 LABELS sensor_id 2 area_id 32
		// TS.ADD temperature:3:11 1548149181 30
		ts.add(key, new Sample(TIMESTAMP_1, VALUE_1), createOptions());
		// TS.ADD temperature:3:11 1548149191 42
		ts.add(key, TIMESTAMP_2, VALUE_2);
		// TS.RANGE temperature:3:11 1548149180 1548149210 AGGREGATION avg 5
		return key;
	}

	@ParameterizedTest
	@RedisTestContextsSource
	void mrange(RedisTestContext context) {
		RedisTimeSeriesCommands<String, String> ts = context.sync();
		String key = populateTimeSeries(ts);
		List<RangeResult<String, String>> results = ts.mrange(RangeOptions.from(0).to(-1).build(),
				LABEL_SENSOR_ID + "=" + SENSOR_ID);
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(key, results.get(0).getKey());
		Assertions.assertEquals(2, results.get(0).getSamples().size());
		Assertions.assertEquals(TIMESTAMP_1, results.get(0).getSamples().get(0).getTimestamp());
		Assertions.assertEquals(VALUE_1, results.get(0).getSamples().get(0).getValue());
		Assertions.assertEquals(TIMESTAMP_2, results.get(0).getSamples().get(1).getTimestamp());
		Assertions.assertEquals(VALUE_2, results.get(0).getSamples().get(1).getValue());
	}

	@ParameterizedTest
	@RedisTestContextsSource
	void mrangeWithLabels(RedisTestContext context) {
		RedisTimeSeriesCommands<String, String> ts = context.sync();
		String key = populateTimeSeries(ts);
		List<RangeResult<String, String>> results = ts.mrangeWithLabels(RangeOptions.from(0).to(-1).build(),
				LABEL_SENSOR_ID + "=" + SENSOR_ID);
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(key, results.get(0).getKey());
		Assertions.assertEquals(2, results.get(0).getSamples().size());
		Assertions.assertEquals(TIMESTAMP_1, results.get(0).getSamples().get(0).getTimestamp());
		Assertions.assertEquals(VALUE_1, results.get(0).getSamples().get(0).getValue());
		Assertions.assertEquals(TIMESTAMP_2, results.get(0).getSamples().get(1).getTimestamp());
		Assertions.assertEquals(VALUE_2, results.get(0).getSamples().get(1).getValue());
		Assertions.assertEquals(2, results.get(0).getLabels().size());
		Assertions.assertEquals(SENSOR_ID, results.get(0).getLabels().get(LABEL_SENSOR_ID));
		Assertions.assertEquals(AREA_ID, results.get(0).getLabels().get(LABEL_AREA_ID));
	}

}