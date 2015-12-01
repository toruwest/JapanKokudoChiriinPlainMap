//package t.n.plainmap.view;
//
//import java.awt.BorderLayout;
//import java.awt.Image;
//import java.awt.Point;
//import java.awt.event.ComponentEvent;
//import java.awt.event.ComponentListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLEventListener;
//import javax.media.opengl.awt.GLJPanel;
//
//import t.n.heightmap.MouseMovementObserver;
//import t.n.heightmap.RepaintCause;
//import t.n.map.TilePosition;
//import t.n.map.common.LonLat;
//import t.n.map.common.LonLatUtil;
//import t.n.map.common.Tile;
//import t.n.plainmap.ITileImageManager;
//import t.n.plainmap.util.TileImageManagerUtil;
//
//public class GLMapPanel extends GLJPanel implements GLEventListener, MouseListener, MouseMotionListener, ComponentListener {
//	private static final Logger logger = Logger.getLogger(MapPanel.class.getSimpleName());
//
////	private ITileImageManager imageManager;
//	private MouseMovementObserver observer;
//
//	private boolean isDragging = false;
////	private int prevMouseX;
////	private int prevMouseY;
//	private Point prevMouse;
//	private float moveX;
//	private float moveY;
//
//	private RepaintCause repaintCause = RepaintCause.init;
//
//	protected int zoomLevel;
//
//	//左上の原点にあるタイル
//	protected Tile originTile;
//
//	//左上の原点にあるタイルの、左上の角のX座標。
//	//FIXME これは、TileImageManagerImplクラスの変数tilePositionNearOrginXと同じ値になる？
//	protected int originX;
//
//	//左上の原点にあるタイルの、左上の角のY座標。
//	protected int originY;
//
//	public GLMapPanel() {
//		//TODO クラスごとにログレベルを設定できないか？開発中にはあるクラスについて詳細なログを出したいのは普通だと思うけど。
//		setLayout(new BorderLayout());
//		logger.setLevel(Level.WARNING);
//	}
//
//	public GLMapPanel(int zoomLevel, ITileImageManager imageManager, MouseMovementObserver observer) {
//		this();
//		this.zoomLevel = zoomLevel;
////		this.imageManager = imageManager;
//		imageManager.init(getWidth(), getHeight());
//		this.observer = observer;
//		addMouseMotionListener(this);
//	}
//
//	public void notifyFetchingCompleted(Tile tile) {
//		repaintCause = RepaintCause.fetchComplete;
//	}
//
////	@Override
////	protected void paintComponent(Graphics g) {
////		super.paintComponent(g);
////		if(imageManager == null) return;
////
////		//画面のリサイズ、ドラッグによる中心位置の移動、ズームレベルの変更に対応する。
////		switch(repaintCause) {
////		case none:
////			break;
////		case init:
////			break;
////		case dragged:
////			imageManager.move(moveX, moveY);
////			break;
////		case resized:
////			imageManager.resize(getWidth(), getHeight());
////			break;
////		case zoomChanged:
////			imageManager.zoom(zoomLevel);
////			break;
////		case fetchComplete:
////			//何もしなくてもいい。
////			break;
////		}
////		repaintCause = RepaintCause.none;
////
////		//ConcurrentModificationException対策
////		List<TilePosition> tilePositionList = new ArrayList<>(imageManager.getTilePositionList());
////		//FIXME 以下はConcurrentModificationException対策しているつもりだけど、やはり例外になってしまう。
////		Map<TilePosition, Tile> positionTileMap = new HashMap<>(imageManager.getPositionTileMap());
////		Map<Tile, Image> tileImageMap = imageManager.getTileImageMap();
////
////		if (tilePositionList == null || positionTileMap == null || tileImageMap == null ) {
////			return;
////		}
////
////		int x = 0, y = 0;
////		boolean isFirstLoop = true;
////		for(TilePosition pos : tilePositionList) {
////			Tile tile = positionTileMap.get(pos);
////			x = pos.getX();
////			y = pos.getY();
////			//マウスカーソルを動かしたときに緯度・経度を表示するために以下の情報が必要となるので、保存しておく。
////			//forループの最初の一回だけ必要。
////			if(isFirstLoop) {
////				originTile = tile;
////				originX = x;
////				originY = y;
////				isFirstLoop = false;
////			}
////			g.drawImage(tileImageMap.get(tile), x, y, null, null);
////		}
////	}
//
//	@Override
//	public void mouseDragged(MouseEvent e) {
////		int x = e.getX();
////		int y = e.getY();
//		Point p = e.getPoint();
//
//		if(isDragging) {
//			moveX = p.x - prevMouse.x;
//			moveY = p.y - prevMouse.y;
//		} else {
//			isDragging = true;
//		}
//
//		// 現在のマウスの位置を保存
////		prevMouseX = x;
////		prevMouseY = y;
//		prevMouse = p;
//		repaintCause = RepaintCause.dragged;
//		repaint();
//	}
//
//	@Override
//	public void mouseMoved(MouseEvent e) {
////		prevMouseX = e.getX();
////		prevMouseY = e.getY();
//		prevMouse = e.getPoint();
//
//		//マウスをクリックせずに動かしたときに、その場所の緯度経度を表示する。
//		// originX,originY(パネルの左上の原点の世界座標)はpaintComponent()実行時に保存しておいたのを使う。
//		LonLat lonlat = TileImageManagerUtil.getLonTatFromScreenCoord(originTile, originX, originY, prevMouse.x, prevMouse.y, zoomLevel);
//		log("  lon:" + LonLatUtil.getLongitudeString(lonlat.getLongitude()) + ", lat:" + LonLatUtil.getLatitudeString(lonlat.getLatitude()));
//		observer.notifyMouseMovingLonLat(LonLatUtil.getLongitudeString(lonlat.getLongitude()), LonLatUtil.getLatitudeString(lonlat.getLatitude()));
//
//		//以下は動作確認用。mouse xとscreen x,mouse yとscreen yはそれぞれ一致するはず。
//		Point p = TileImageManagerUtil.getScreenCoordFromLonTat(lonlat, originTile, originX, originY, zoomLevel);
//		log(String.format("mouse x:%d, screen x:%d, mouse y:%d, screen y:%d%n", prevMouse.x, p.x, prevMouse.y, p.y));
//	}
//
//	public void zoomLevelChange(int value) {
//		zoomLevel = value;
//		repaintCause = RepaintCause.zoomChanged;
//		repaint();
//	}
//
//	@Override
//	public void mouseReleased(MouseEvent e) {
//		isDragging  = false;
//	}
//
//	@Override
//	public void mouseClicked(MouseEvent e) {}
//
//	@Override
//	public void mousePressed(MouseEvent e) {}
//
//	@Override
//	public void mouseEntered(MouseEvent e) {}
//
//	@Override
//	public void mouseExited(MouseEvent e) {}
//
//
//	// 画面のリサイズによるイベントを捕捉して、地図を更新。
//	@Override
//	public void componentResized(ComponentEvent evt) {
//		repaintCause = RepaintCause.resized;
//		repaint();
//	}
//
//	@Override
//	public void componentMoved(ComponentEvent e) {}
//
//	@Override
//	public void componentShown(ComponentEvent e) {}
//
//	@Override
//	public void componentHidden(ComponentEvent e) {}
//
//	private void log(String msg) {
//		if(logger.isLoggable(Level.FINE)) {
//			logger.info(msg);
//		}
//	}
//
//	@Override
//	public void init(GLAutoDrawable drawable) {
////		imageManager.init(getWidth(), getHeight());
//
//	}
//
//	@Override
//	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
//			int height) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void display(GLAutoDrawable drawable) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void dispose(GLAutoDrawable drawable) {
//		// TODO Auto-generated method stub
//
//	}
//
//	private void xxx() {
//
//	//ConcurrentModificationException対策
//	List<TilePosition> tilePositionList = new ArrayList<>(imageManager.getTilePositionList());
//	//FIXME 以下はConcurrentModificationException対策しているつもりだけど、やはり例外になってしまう。
//	Map<TilePosition, Tile> positionTileMap = new HashMap<>(imageManager.getPositionTileMap());
//	Map<Tile, Image> tileImageMap = imageManager.getTileImageMap();
//
//	if (tilePositionList == null || positionTileMap == null || tileImageMap == null ) {
//		return;
//	}
//
//	int x = 0, y = 0;
//	boolean isFirstLoop = true;
//	for(TilePosition pos : tilePositionList) {
//		Tile tile = positionTileMap.get(pos);
//		x = pos.getX();
//		y = pos.getY();
//		//マウスカーソルを動かしたときに緯度・経度を表示するために以下の情報が必要となるので、保存しておく。
//		//forループの最初の一回だけ必要。
//		if(isFirstLoop) {
//			originTile = tile;
//			originX = x;
//			originY = y;
//			isFirstLoop = false;
//		}
//		g.drawImage(tileImageMap.get(tile), x, y, null, null);
//	}
//
//}
//}