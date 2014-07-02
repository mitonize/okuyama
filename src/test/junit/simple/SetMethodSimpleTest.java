package test.junit.simple;

import static org.junit.Assert.*;

import java.util.Random;

import okuyama.imdst.client.OkuyamaClient;
import okuyama.imdst.client.OkuyamaClientException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import test.junit.MethodTestHelper;

/**
 * setメソッドの簡単なテスト。
 * @author s-ito
 *
 */
public class SetMethodSimpleTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static MethodTestHelper helper = new MethodTestHelper();

	private OkuyamaClient okuyamaClient;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		SetMethodSimpleTest.helper.init();
		// okuyamaに接続
		this.okuyamaClient =  SetMethodSimpleTest.helper.getConnectedOkuyamaClient();
	}

	@After
	public void tearDown() throws Exception {
		this.okuyamaClient.close();
	}

	@Test
	public void setに成功してtrueを返す() throws Exception {
		assertTrue(this.okuyamaClient.setValue(SetMethodSimpleTest.helper.createTestDataKey(false),
												SetMethodSimpleTest.helper.createTestDataValue(false)));
	}

	@Test
	public void setするキーがnullのため例外発生() throws Exception {
		thrown.expect(OkuyamaClientException.class);
		thrown.expectMessage("The blank is not admitted on a key");
		this.okuyamaClient.setValue(null, "foo");
	}

	@Test
	public void setするキーが328B以上のため例外発生() throws Exception {
		// サイズは3MBのキーを作成
		final int keySize = 329;
		Random rnd = new Random();
		StringBuilder bigKeyBuilder = new StringBuilder(keySize);
		for (int i = 0;i < keySize;i++) {
			bigKeyBuilder = bigKeyBuilder.append(rnd.nextInt(10));
		}
		// テスト
		thrown.expect(OkuyamaClientException.class);
		thrown.expectMessage("Save Key Max Size 327 Byte");
		this.okuyamaClient.setValue(bigKeyBuilder.toString(), "foo");
	}

	@Test
	public void サーバとのセッションがないため例外発生() throws Exception {
		thrown.expect(OkuyamaClientException.class);
		thrown.expectMessage("No ServerConnect!!");
		this.okuyamaClient.close();
		this.okuyamaClient.setValue("foo", "bar");
	}

}
