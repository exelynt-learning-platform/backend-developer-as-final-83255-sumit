package com.exelynt.resource_booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
		"jwt.expirationMs=86400000"
})
class ResourceBookingApplicationTests {

	@Test
	void contextLoads() {
	}

}
