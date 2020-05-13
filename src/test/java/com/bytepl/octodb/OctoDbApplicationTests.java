package com.bytepl.octodb;

import com.bytepl.octodb.batch.io.impl.FileCrudOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:application.properties")
public class OctoDbApplicationTests {
	@Autowired
	private FileCrudOperation fileCrudOperation;

	@Test
	public void contextLoads() {
	}

	@Test
	public void createDatabase(){

	}

}
