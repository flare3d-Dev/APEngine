package org.cove.ape.util;

import flash.display.Graphics;
import flash.display.Sprite;

/**
 * 创建图形设备接口
 * @author bruce
 *
 */
public interface IGraphicsCreator {	
	Graphics create(Sprite sprite);
}
