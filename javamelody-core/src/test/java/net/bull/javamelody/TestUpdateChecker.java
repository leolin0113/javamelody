/*
 * Copyright 2008-2016 by Emeric Vernat
 *
 *     This file is part of Java Melody.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.bull.javamelody;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

/**
 * Test unitaire de la classe UpdateChecker.
 * @author Emeric Vernat
 */
public class TestUpdateChecker {

	/** Before. */
	@Before
	public void setUp() {
		Utils.initialize();
	}

	/** Test. */
	@Test
	public void testInit() {
		Utils.setProperty(Parameter.UPDATE_CHECK_DISABLED, "true");
		UpdateChecker.init(null, null, null);

	}

	/** Test.
	 * @throws IOException e */
	@Test
	public void testCheckForUpdate() throws IOException {
		final ServletContext context = createNiceMock(ServletContext.class);
		expect(context.getMajorVersion()).andReturn(2).anyTimes();
		expect(context.getMinorVersion()).andReturn(5).anyTimes();
		expect(context.getContextPath()).andReturn("/test").anyTimes();
		replay(context);
		Parameters.initialize(context);
		verify(context);
		final Collector collector = new Collector("test",
				Arrays.asList(new Counter("http", null), new Counter("sql", null)));
		JRobin.initBackendFactory(new Timer(getClass().getSimpleName(), true));
		assertNotNull(new SessionListener());
		collector.collectWithoutErrors(Arrays.asList(new JavaInformations(null, true)));
		final String serverUrl = "http://dummy";
		final UpdateChecker updateCheckerCollectorServer = UpdateChecker.createForTest(null,
				UpdateChecker.COLLECTOR_SERVER_APPLICATION_TYPE, serverUrl);
		try {
			updateCheckerCollectorServer.checkForUpdate();
		} catch (final UnknownHostException e) {
			// UnknownHostException is ok for url http://dummy
			assertNotNull(updateCheckerCollectorServer);
		}
		final UpdateChecker updateChecker = UpdateChecker.createForTest(collector, "Classic",
				serverUrl);
		try {
			updateChecker.checkForUpdate();
		} catch (final UnknownHostException e) {
			// UnknownHostException is ok for url http://dummy
			assertNotNull(updateChecker);
		}
	}
}
