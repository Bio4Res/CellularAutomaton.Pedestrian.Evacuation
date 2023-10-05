package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.geometry._2d;

import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.gui.Canvas;

import java.awt.*;

/**
 * Class for representing an axis-aligned rectangle.
 *
 * @param bottom bottom coordinate of rectangle.
 * @param left   left coordinate of rectangle.
 * @param height height of rectangle.
 * @param width  width of rectangle.
 * @author Pepe Gallardo
 */
public record Rectangle(int bottom, int left, int height, int width) {
  public Rectangle {
    if (height < 0) {
      throw new IllegalArgumentException("Rectangle: height cannot be negative");
    }
    if (width < 0) {
      throw new IllegalArgumentException("Rectangle: width cannot be negative");
    }
  }

  public int top() {
    return bottom + height - 1;
  }

  public int right() {
    return left + width - 1;
  }

  public boolean intersects(Rectangle that) {
    return this.left <= that.right() && this.right() >= that.left && this.top() >= that.bottom && this.bottom <= that.top();
  }

  public boolean intersects(int row, int column) {
    return row >= bottom && row <= top() && column >= left && column <= right();
  }

  public boolean intersects(Location location) {
    return intersects(location.row(), location.column());
  }

  public boolean intersectsAny(Iterable<Rectangle> iterable) {
    for (var element : iterable) {
      if (element.intersects(this)) {
        return true;
      }
    }
    return false;
  }

  public boolean contains(Rectangle that) {
    return that.bottom >= this.bottom && that.top() <= this.top() && that.left >= this.left && that.right() <= this.right();
  }

  private int _manhattanDistance(int x1, int y1, int x2, int y2) {
    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
  }

  public int manhattanDistance(int row, int column) {
    var atLeft = column < this.left;
    var atRight = this.right() < column;
    var atBottom = row < this.bottom;
    var atTop = this.top() < row;

    if (atTop && atLeft) {
      return _manhattanDistance(this.left, this.top(), column, row);
    }
    if (atLeft && atBottom) {
      return _manhattanDistance(this.left, this.bottom, column, row);
    }
    if (atBottom && atRight) {
      return _manhattanDistance(this.right(), this.bottom, column, row);
    }
    if (atRight && atTop) {
      return _manhattanDistance(this.right(), this.top(), column, row);
    }
    if (atLeft) {
      return this.left - column;
    }
    if (atRight) {
      return column - this.right();
    }
    if (atBottom) {
      return this.bottom - row;
    }
    if (atTop) {
      return row - this.top();
    }
    // rectangles intersect
    return 0;
  }

  public int manhattanDistance(Location location) {
    return manhattanDistance(location.row(), location.column());
  }

  public void paint(Canvas canvas, Color fillColor, Color outlineColor) {
    var graphics2D = canvas.graphics2D();
    graphics2D.setColor(fillColor);
    graphics2D.fillRect(left(), bottom(), width(), height());
    graphics2D.setColor(outlineColor);
    graphics2D.drawRect(left(), bottom(), width(), height());
  }

  public int distanceSqr(int row, int column) {
    var dh = Math.max(left() - column, Math.max(0, column - right()));
    var dv = Math.max(bottom() - row, Math.max(0, row - top()));
    return dh*dh + dv*dv;
  }

  public double distance(int row, int column) {
    return Math.sqrt(distanceSqr(row, column));
  }

  public double distance(Location location) {
    return distance(location.row(), location.column());
  }
}
