package t.n.plainmap;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import t.n.map.common.LightWeightTile;
import t.n.plainmap.util.TiledMapUtil;

public class TileMapUtilTest {

	@Test
	public void testGenerateLocalFilename() {
		File savindDir = new File(".");
		String expect = "./17/01158430051898.png";
		assertEquals(expect, TiledMapUtil.generateLocalFilename(savindDir, new LightWeightTile(17, 115843, 51898)));
	}

	@Test
	public void testGenerateImageURI() {
		for(int level = 0; level <= 18; level++) {
			assertEquals(expect[level], TiledMapUtil.generateImageURI(level, x[level], y[level]));
		}
	}

	String[] expect = {
			"http://cyberjapandata.gsi.go.jp/sqras/all/GLMD/latest/0/00/00/00/00/00/00/00000000000000.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/GLMD/latest/1/00/00/00/00/00/00/00000000000000.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/GLMD/latest/2/00/00/00/00/00/00/00000000000000.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/GLMD/latest/3/00/00/00/00/00/00/00000050000001.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/GLMD/latest/4/00/00/00/00/00/10/00000120000004.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/JAIS/latest/5/00/00/00/00/00/21/00000260000011.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/JAIS/latest/6/00/00/00/00/00/52/00000540000023.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/JAIS/latest/7/00/00/00/00/10/14/00001120000049.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/JAIS/latest/8/00/00/00/00/21/20/00002290000100.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/BAFD1000K/latest/9/00/00/00/00/42/50/00004570000201.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/BAFD1000K/latest/10/00/00/00/00/94/10/00009110000404.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/BAFD1000K/latest/11/00/00/00/10/88/10/00018180000808.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/BAFD200K/latest/12/00/00/00/31/66/31/00036370001614.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/BAFD200K/latest/13/00/00/00/73/22/72/00072740003227.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/BAFD200K/latest/14/00/00/10/46/54/55/00145520006457.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/DJBMM/latest/15/00/00/21/92/19/01/00291050012914.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/DJBMM/latest/16/00/00/52/85/28/12/00582120025828.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/DJBMM/latest/17/00/10/15/51/88/49/01158430051898.png",
			"http://cyberjapandata.gsi.go.jp/sqras/all/FGD/latest/18/00/21/30/13/67/89/02316810103797.png"
	};

	int[] x = {0,0,0,5,12,26,54,112,229,457,911,1818,3637,7274,14552,29105,58212,115843,231681};
	int[] y = {0,0,0,1,4,11,23,49,100,201,404,808,1614,3227,6457,12914,25828,51898,103797};
}
