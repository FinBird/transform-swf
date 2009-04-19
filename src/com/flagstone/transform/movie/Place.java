/*
 * PlaceObject.java
 * Transform
 * 
 * Copyright (c) 2001-2008 Flagstone Software Ltd. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of Flagstone Software Ltd. nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.flagstone.transform.movie;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFContext;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.coder.SWFEncoder;
import com.flagstone.transform.movie.datatype.ColorTransform;
import com.flagstone.transform.movie.datatype.CoordTransform;

//TODO(doc) Review
/**
 * PlaceObject is used to add an object (shape, button, etc.) to the Flash
 * Player's display list.
 * 
 * <p>When adding an object to the display list a coordinate transform can be 
 * applied to the object. This is principally used to specify the location of the
 * object when it is drawn on the screen however more complex coordinate
 * transforms can also be specified such as rotating or scaling the object
 * without changing the original definition.</p>
 * 
 * <p>Similarly the color transform allows the color of the object to be changed
 * when it is displayed without changing the original definition. The PlaceObject 
 * class only supports opaque colours so although the ColorTransform supports 
 * transparent colours this information is ignored by the Flash Player. The colour 
 * transform is optional and may be set to null, reducing the size of the 
 * PlaceObject instruction when it is encoded.</p>
 * 
 * @see Place2
 * @see Remove
 * @see Remove2
 */
//TODO(api) Add protected method for PlaceBuilder
public final class Place implements MovieTag
{
	//TODO(code) Consider replacing with StringBuilder for optional fields
	private static final String FORMAT = "Place: { layer=%d; identifier=%d; transform=%s; colorTransform=%s; }";

	private int identifier;
	private int layer;
	private CoordTransform transform;
	private ColorTransform colorTransform;
	
	private transient int start;
	private transient int end;
	private transient int length;

	//TODO(doc)
	public Place(final SWFDecoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		length = coder.readWord(2, false) & 0x3F;
		
		if (length == 0x3F) {
			length = coder.readWord(4, false);
		}
		
		end = coder.getPointer() + (length << 3);

		identifier = coder.readWord(2, false);
		layer = coder.readWord(2, false);
		transform = new CoordTransform(coder);

		if (coder.getPointer() < end) {
			colorTransform = new ColorTransform(coder, context);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}

	/**
	 * Creates a PlaceObject object that places an object with the
	 * identifier into the display list layer at the specified coordinates
	 * (x,y).
	 * 
	 * @param uid
	 *            the unique identifier for the object to the placed on the
	 *            display list. Must be in the range 1..65535.
	 * @param aLayer
	 *            the layer in the display list where the object will be placed.
	 *            Must be in the range 1..65535.
	 * @param xLocation
	 *            the x-coordinate where the object will be drawn.
	 * @param yLocation
	 *            the y-coordinate where the object will be drawn.
	 */
	//TODO(api) consider removing
	public Place(int uid, int aLayer, int xLocation, int yLocation)
	{
		setIdentifier(uid);
		setLayer(aLayer);
		setTransform(CoordTransform.translate(xLocation, yLocation));
	}

	/**
	 * Creates a PlaceObject object that places the object with the
	 * identifier at the specified layer with the coordinate transform.
	 * 
	 * @param uid
	 *            the unique identifier for the object to the placed on the
	 *            display list. Must be in the range 1..65535.
	 * @param aLayer
	 *            the layer in the display list where the object will be placed.
	 *            Must be in the range 1..65535.
	 * @param aTransform
	 *            an CoordTransform object that defines the orientation, size
	 *            and location of the object when it is drawn. Must not be null.
	 */
	public Place(int uid, int aLayer, CoordTransform aTransform)
	{
		setIdentifier(uid);
		setLayer(aLayer);
		setTransform(aTransform);
	}

	/**
	 * Creates a PlaceObject object that places the the object with the
	 * identifier at the specified layer, coordinate transform and colour
	 * transform.
	 * 
	 * @param uid
	 *            the unique identifier for the object to the placed on the
	 *            display list. Must be in the range 1..65535.
	 * @param aLayer
	 *            the layer in the display list where the object will be placed.
	 * @param aTransform
	 *            an CoordTransform object that defines the orientation, size
	 *            and location of the object when it is drawn. Must not be null.
	 * @param aColorTransform
	 *            an ColorTransform object that defines the colour of the
	 *            object when it is drawn.
	 */
	public Place(int uid, int aLayer,
							CoordTransform aTransform, 
							ColorTransform aColorTransform)
	{
		setIdentifier(uid);
		setLayer(aLayer);
		setTransform(aTransform);
		setColorTransform(aColorTransform);
	}
	
	//TODO(doc)
	public Place(Place object) {
		identifier = object.identifier;
		layer = object.layer;
		transform = object.transform;
		colorTransform = object.colorTransform;
	}

	/**
	 * Returns the identifier of the object to add to the display list.
	 */
	public int getIdentifier()
	{
		return identifier;
	}

	/**
	 * Returns the layer that defines the order in which objects are displayed.
	 */
	public int getLayer()
	{
		return layer;
	}

	/**
	 * Returns the transform that defines the position where the object is
	 * displayed.
	 */
	public CoordTransform getTransform()
	{
		return transform;
	}

	/**
	 * Returns the colour transform that defines any colour effects applied when
	 * the object is displayed. May be null if no transform is defined.
	 */
	public ColorTransform getColorTransform()
	{
		return colorTransform;
	}

	/**
	 * Sets the identifier of the object that will be added to the display list.
	 * 
	 * @param uid
	 *            the unique identifier for the object to the placed on the
	 *            display list. Must be in the range 1..65535.
	 */
	public void setIdentifier(int uid)
	{
		if (uid < 1 || uid > 65535) {
			throw new IllegalArgumentException(Strings.IDENTIFIER_OUT_OF_RANGE);
		}
		identifier = uid;
	}

	/**
	 * Sets the layer that defines the order in which objects are displayed.
	 * 
	 * @param aNumber
	 *            the layer in the display list where the object will be placed.
	 *            Must be in the range 1..65535.
	 */
	public void setLayer(int aNumber)
	{
		if (aNumber < 1 || aNumber > 65535) {
			throw new IllegalArgumentException(Strings.LAYER_OUT_OF_RANGE);
		}
		layer = aNumber;
	}

	/**
	 * Sets the transform that defines the position where the object is
	 * displayed.
	 * 
	 * @param aTransform
	 *            an CoordTransform object that defines the orientation, size
	 *            and location of the object when it is drawn. Must not be null.
	 */
	public void setTransform(CoordTransform aTransform)
	{
		if (aTransform == null) {
			throw new IllegalArgumentException(Strings.OBJECT_CANNOT_BE_NULL);
		}
		transform = aTransform;
	}

	/**
	 * Sets the colour transform that defines any colour effects applied when
	 * the object is displayed.
	 * 
	 * @param aColorTransform
	 *            an ColorTransform object that defines the colour of the
	 *            object when it is drawn. May be set to null.
	 */
	public void setColorTransform(ColorTransform aColorTransform)
	{
		colorTransform = aColorTransform;
	}

	/**
	 * Creates and returns a deep copy of this object.
	 */
	//TODO(doc) remove
	public Place copy()
	{
		return new Place(this);
	}

	@Override
	public String toString()
	{
		return String.format(FORMAT, identifier, layer, transform, colorTransform);
	}

	public int prepareToEncode(final SWFEncoder coder, final SWFContext context)
	{
		length = 4;
		length += transform.prepareToEncode(coder, context);
		//TODO(optimise) replace with if statement ?
		length += colorTransform == null ? 0 : colorTransform.prepareToEncode(coder, context);

		return 2 + length;
	}

	public void encode(final SWFEncoder coder, final SWFContext context) throws CoderException
	{
		start = coder.getPointer();
		coder.writeWord((Types.PLACE << 6) | length, 2);
		end = coder.getPointer() + (length << 3); //TODO(optimise) end = start +16

		coder.writeWord(identifier, 2);
		coder.writeWord(layer, 2);
		transform.encode(coder, context);

		if (colorTransform != null) {
			colorTransform.encode(coder, context);
		}

		if (coder.getPointer() != end) {
			throw new CoderException(getClass().getName(), start >> 3, length,
					(coder.getPointer() - end) >> 3);
		}
	}
}
