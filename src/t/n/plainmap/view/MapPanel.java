package t.n.plainmap.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import t.n.map.common.TilePosition;
import t.n.map.common.LightWeightTile;
import t.n.map.common.LonLat;
import t.n.map.common.util.LonLatUtil;
import t.n.map.common.util.TileImageManagerUtil;
import t.n.plainmap.ITileImageManager;
import t.n.plainmap.MouseMovementObserver;

public class MapPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {
	private static final Logger logger = Logger.getLogger(MapPanel.class.getSimpleName());

	private static final String COPYRIGHT = "この地図は、国土地理院発行の電子国土基本図などを使っています。";
	private final Shape text;
	private final Shape textBack;
	private final Rectangle textRect;

	protected ITileImageManager imageManager;
	private MouseMovementObserver observer;

	private boolean isDragging = false;
	private Point prevMouse;
	private float moveX;
	private float moveY;

	private RepaintCause repaintCause = RepaintCause.init;

	protected int zoomLevel;

	//原点にあるタイルの、左上の角のX座標。負の値あるいはゼロになる。
	//これは、ImageTileGroupクラスの変数tilePositionNearOrginXと同じ値になる？
	protected int originX;

	//原点にあるタイルの、左上の角のY座標。負の値あるいはゼロになる。
	protected int originY;

	private int tileSize;

	public MapPanel() {
		setLayout(new BorderLayout());
		logger.setLevel(Level.WARNING);

		Font font = new Font(Font.SERIF, Font.PLAIN, 14);
        FontRenderContext frc = new FontRenderContext(null, true, true);
        text = new TextLayout(COPYRIGHT, font, frc).getOutline(null);
		textRect = text.getBounds();
        textBack = new Rectangle(textRect.width, textRect.height);
	}

	public MapPanel(int zoomLevel, int tileSize, ITileImageManager imageManager, MouseMovementObserver observer) {
		this();
		this.zoomLevel = zoomLevel;
		this.tileSize = tileSize;
		this.imageManager = imageManager;
		this.observer = observer;
		addMouseMotionListener(this);
	}

	public void notifyFetchingCompleted(LightWeightTile tile) {
		repaintCause = RepaintCause.fetchComplete;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(imageManager == null) return;

		//画面のリサイズ、ドラッグによる中心位置の移動、ズームレベルの変更に対応する。
		switch(repaintCause) {
		case none:
			break;
		case init:
			imageManager.init(getWidth(), getHeight(), zoomLevel);
			break;
		case dragged:
			imageManager.move(moveX, moveY);
			break;
		case resized:
			imageManager.resize(getWidth(), getHeight());
			break;
		case zoomChanged:
			imageManager.zoom(zoomLevel);
			break;
		case fetchComplete:
			//何もしなくてもいい。
			break;
		}
		repaintCause = RepaintCause.none;

		//ConcurrentModificationException対策
		List<TilePosition> tilePositionList = new ArrayList<>(imageManager.getTilePositionList());

		if (tilePositionList == null ) {
			return;
		}

		int offsetX = imageManager.getTilePositionNearOriginX();
		int offsetY = imageManager.getTilePositionNearOriginY();
		int originTileNoX = imageManager.getOriginTileNoX();
		int originTileNoY = imageManager.getOriginTileNoY();

		int x = 0, y = 0;
		boolean isFirstLoop = true;
		for(TilePosition pos : tilePositionList) {
			LightWeightTile tile = imageManager.getTileAt(pos);

			//マウスカーソルを動かしたときに緯度・経度を表示するために以下の情報が必要となるので、保存しておく。
			//forループの最初の一回だけ必要。
			x = (pos.getX() - originTileNoX) * tileSize + offsetX;
			y = (pos.getY() - originTileNoY) * tileSize + offsetY;
			if(isFirstLoop) {
				originX = x;
				originY = y;
				isFirstLoop = false;
			}
			System.out.print("tile no:" + pos.getX() + "," + pos.getY() + ", x:" + x + ", y:" + y);

			if(tile != null) {
				g.drawImage(tile.getImage(), x, y, null, null);
				System.out.println(": tile is NOT null");
			} else {
				System.out.println(": tile is null");
			}
		}
		showCopyrightNotice(g);

		System.out.println("--------------");
	}

	//国土地理院の電子国土地図の利用規約を表示する。
	//警告：地図になんらかの加工を施す場合、以下の内容をそのまま残すか、あるいは改変・流用する者の責任において、適切な表記を行ってください。
	//（加工後の地図が、国土地理院の著作物であると誤解されるような表記は、不適切である可能性があります)
	//詳細は、http://www.gsi.go.jp/LAW/2930-index.htmlを参照。
	//また、流用・改変後のソースコードについて、オリジナル著作者の著作物であると誤解されることの無いよう、注意してください。
	protected void showCopyrightNotice(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
//		g2d.drawString(COPYRIGHT, 50, getHeight() - 10);

		//右寄せ。描画領域の背景の色を変える。
        g2d.setPaint(Color.CYAN);//文字の背景
        g2d.translate(getWidth() - textRect.width - 5, getHeight() - textRect.height*1.5);
		g2d.fill(textBack);
		g2d.translate(0, textRect.height);
		g2d.setPaint(Color.BLACK);//文字色
        g2d.draw(text);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();

		if(isDragging) {
			moveX = p.x - prevMouse.x;
			moveY = p.y - prevMouse.y;
		} else {
			isDragging = true;
		}

		// 現在のマウスの位置を保存
		prevMouse = p;
		repaintCause = RepaintCause.dragged;
		repaint();
	}

	//マウスをクリックせずに動かしたときに、その場所の緯度経度を表示する。
	@Override
	public void mouseMoved(MouseEvent e) {
		prevMouse = e.getPoint();
		int originTileNoX = imageManager.getOriginTileNoX();
		int originTileNoY = imageManager.getOriginTileNoY();

		// originX,originY(パネルの左上の原点の世界座標)はpaintComponent()実行時に保存しておいたのを使う。
		LonLat lonlat = TileImageManagerUtil.getLonTatFromScreenCoord(originTileNoX, originTileNoY, originX, originY, prevMouse.x, prevMouse.y, zoomLevel);
		log("  lon:" + LonLatUtil.getLongitudeJapaneseString(lonlat.getLongitude()) + ", lat:" + LonLatUtil.getLatitudeJapaneseString(lonlat.getLatitude()));
		observer.notifyMouseMovingLonLat(LonLatUtil.getLongitudeJapaneseString(lonlat.getLongitude()), LonLatUtil.getLatitudeJapaneseString(lonlat.getLatitude()));

		//以下は動作確認用。mouse xとscreen x,mouse yとscreen yはそれぞれ一致するはず。
		Point p = TileImageManagerUtil.getScreenCoordFromLonTat(lonlat, originTileNoX, originTileNoY, originX, originY, zoomLevel);
		log(String.format("mouse x:%d, screen x:%d, mouse y:%d, screen y:%d%n", prevMouse.x, p.x, prevMouse.y, p.y));
	}

	public void zoomLevelChange(int value) {
		zoomLevel = value;
		repaintCause = RepaintCause.zoomChanged;
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isDragging  = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}


	// 画面のリサイズによるイベントを捕捉して、地図を更新。
	@Override
	public void componentResized(ComponentEvent evt) {
		repaintCause = RepaintCause.resized;
		repaint();
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

	private void log(String msg) {
		if(logger.isLoggable(Level.FINE)) {
			logger.info(msg);
		}
	}

}