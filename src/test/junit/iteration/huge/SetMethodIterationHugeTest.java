package test.junit.iteration.huge;

import static org.junit.Assert.*;
import okuyama.imdst.client.OkuyamaClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.junit.MethodTestHelper;

/**
 * 巨大なデータに対するsetメソッドの繰り返しテスト。
 * 
 * @author T.Okuyama
 * @license GPL(Lv3)
 *
 */
public class SetMethodIterationHugeTest {

	private static MethodTestHelper helper = new MethodTestHelper();

	private OkuyamaClient okuyamaClient;

	@Before
	public void setUp() throws Exception {
		SetMethodIterationHugeTest.helper.init();
		SetMethodIterationHugeTest.helper.initBigTestData();
		// okuyamaに接続
		this.okuyamaClient =  SetMethodIterationHugeTest.helper.getConnectedOkuyamaClient();
	}

	@After
	public void tearDown() throws Exception {
		SetMethodIterationHugeTest.helper.deleteAllData();
		this.okuyamaClient.close();
	}

	@Test
	public void 巨大なデータを500個setする() throws Exception {
		for (int i = 0;i < 500;i++) {
			assertTrue(this.okuyamaClient.setValue(SetMethodIterationHugeTest.helper.createTestDataKey(true, i),
												SetMethodIterationHugeTest.helper.createTestDataValue(true, i)));
		}
	}

	@Test
	public void 値をObjectとした巨大なデータを500個setする() throws Exception {
		for (int i = 0;i < 500;i++) {
			assertTrue(this.okuyamaClient.setObjectValue(SetMethodIterationHugeTest.helper.createTestDataKey(true, i),
												SetMethodIterationHugeTest.helper.createTestDataValue(true, i)));
		}
	}
}
