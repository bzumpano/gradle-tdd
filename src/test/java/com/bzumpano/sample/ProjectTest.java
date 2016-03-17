package com.bzumpano.sample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTest.class)
@WebAppConfiguration
public class ProjectTest {

    @Autowired
    private JdbcTemplate template;

    @Test
    public void testDefaultSettings() throws Exception {
        assertEquals(this.template.queryForObject("SELECT COUNT(*) from PERSON", Integer.class).intValue(), 1);
    }

    @Test
    public void contextLoads() {
    }

}
