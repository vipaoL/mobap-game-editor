/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vipaol.mg.editor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
/**
 *
 * @author vipaol
 */
public class EditorCanvas extends Canvas implements MouseListener, MouseMotionListener, MouseWheelListener {

    int centrPointRadius = 2;
    int tick = 0;
    int offsetX = 0;
    int offsetY = 0;
    int offsetXWhenPressed = 0;
    int offsetYWhenPressed = 0;
    int dMouseX = 0;
    int dMouseY = 0;
    //short[] input = {0/*, 1, 50, 50, 100, 150, 2, 50, 50, 30, 90, 60, 100, 100, 0*/};
    short currPlacingID = 0;
    int relMouseX = 0;
    int relMouseY = 0;
    int mouseOnCanvX = 0;
    int mouseOnCanvY = 0;
    Elements elements = new Elements();
    MGStruct mgStruct = new MGStruct();
    int selected = 0;
    public int carX = 0;
    public int carY = 0;
    int carbodyLength = 240;
    int carbodyHeight = 40;
    int wheelRadius = 40;
    int spawnX = 0 - (carbodyLength / 2 - wheelRadius);
    int spawnY = 0 - wheelRadius / 2 * 3 - 2;
    int lwX = carX - (carbodyLength / 2 - wheelRadius);
    int lwY = carY + wheelRadius / 2;
    int rwX = carX + (carbodyLength / 2 - wheelRadius);
    int rwY = carY + wheelRadius / 2;
    int zoomBase = 1000;
    int zoomOut = zoomBase;

    public EditorCanvas() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    // paint the canvas
    @Override
    public void paint(Graphics g) {
        drawCar(g);
        // set Font
        g.setFont(new Font("Bold", 1, 20));

        // draw a string
        //g.drawString("It's a Canvas!", mouseX, mouseY);
        for (int i = 0; i < mgStruct.getBufSize(); i++) {
            short structID = mgStruct.readBuf(i)[0];
            //System.out.println("id" + structID);
            if (i == selected) {
                // set color to red
                g.setColor(new Color(0x8888ff));
            } else {
                // set color to red
                g.setColor(new Color(0x4444ff));
            }
            if (structID == 0) {
                break;
            }
            drawElement(g, structID, mgStruct.readBuf(i));
        }
        g.setColor(new Color(0x00ff00));
        g.fillRect(calcX(0) - centrPointRadius, calcY(0) - centrPointRadius, centrPointRadius * 2, centrPointRadius * 2);

        g.setColor(new Color(0xffffff));
        g.fillArc(calcX(relMouseX) - 2, calcY(relMouseY) - 2, 4, 4, 0, 360);
        
        short x = (short) relMouseX;
        short y = (short) relMouseY;
        String hint = x + " " + y;
        if (elements.isBusy) { // preview
            short id = elements.currentPlacing[0];
            int step = elements.step;
            if ((step > 1 | mgStruct.clicks[id] < 2) | elements.isEditing) {
                elements.calcArgs(id, step, x, y);
                if ((id == 3 | id == 5) & step > 1) {
                    hint = String.valueOf(elements.currentPlacing[3]);
                }
                drawElement(g, id, elements.currentPlacing);
            }
        }
        g.drawString(hint, mouseOnCanvX + 30, mouseOnCanvY + 30);
    }

    public void drawElement(Graphics g, short id, short[] data) {
        if (id == 1) {
            int x = data[1];
            int y = data[2];
            int r = centrPointRadius;
            Color prevCol = g.getColor();
            g.setColor(new Color(0xff0000));
            g.fillOval(calcX(x) - r, calcY(y) - r, r * 2, r * 2);
            g.setColor(prevCol);
        } else if (id == 2) { // line
            int x1 = data[1];
            int y1 = data[2];
            int x2 = data[3];
            int y2 = data[4];
            g.drawLine(calcX(x1), calcY(y1), calcX(x2), calcY(y2));
        } else if (id == 3) { // arc
            int x = data[1];
            int y = data[2];
            int r = data[3];
            int ang = data[4];
            int offset = data[5];
            int kx = data[6];
            int ky = data[7];
            int zoomed2R = applyZoom(r) * 2;
            g.drawArc(calcX(x - r), calcY(y - r), zoomed2R * kx / 100, zoomed2R * kx / 100, offset, ang);
        }
    }

    private int calcX(int x) {
        return x * 1000 / zoomOut + offsetX;
    }

    private int calcY(int y) {
        return y * 1000 / zoomOut + offsetY;
    }
    
    private int revCalcX(int x) {
        return (x - offsetX) * zoomOut / 1000;
    }

    private int revCalcY(int y) {
        return (y - offsetY) * zoomOut / 1000;
    }
    
    private int applyZoom(int a) {
        return a * 1000 / zoomOut;
    }

    private int revApplyZoom(int a) {
        return a * zoomOut / 1000;
    }

    public void mousePressed(MouseEvent e) {
        relMouseX = revCalcX(e.getX());
        relMouseY = revCalcY(e.getY());
        mouseOnCanvX = e.getX();
        mouseOnCanvY = e.getY();
        repaint();
    }

    boolean dragging = false;

    public void mouseReleased(MouseEvent e) {
        if (currPlacingID != 0 & !dragging) {
            if (elements.clicked(relMouseX, relMouseY)) {
                currPlacingID = 0;
            }
        } else {
            dragging = false;
        }
        relMouseX = revCalcX(e.getX());
        relMouseY = revCalcY(e.getY());
        repaint();
    }

    public void mouseDragged(MouseEvent e) {
        dMouseX = e.getX() - mouseOnCanvX;
        dMouseY = e.getY() - mouseOnCanvY;
        mouseOnCanvX = e.getX();
        mouseOnCanvY = e.getY();
        offsetX += (dMouseX);
        offsetY += (dMouseY);
        dragging = true;
        repaint();
    }

    boolean entered = false;
    public void mouseEntered(MouseEvent e) {
        entered = true;
        repaint();
    }

    public void mouseExited(MouseEvent e) {
        entered = false;
        elements.cancel();
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseOnCanvX = e.getX();
        mouseOnCanvY = e.getY();
        relMouseX = revCalcX(mouseOnCanvX);
        relMouseY = revCalcY(mouseOnCanvY);
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            zoomOut += e.getWheelRotation();
        } else {
            zoomOut += 100 * e.getWheelRotation();
        }
        if (zoomOut <= 0) {
            zoomOut = 1;
        }
        offsetX = mouseOnCanvX - applyZoom(relMouseX);
        offsetY = mouseOnCanvY - applyZoom(relMouseY);
        repaint();
    }

    int selectedOption = -1;
    int selectedOptionInUpperRow = -1;
    int selectedInList = 0;
    int selectedIn3Row = -1;
    public class Elements {

        short[] currentPlacing;
        private boolean isBusy = false;
        private boolean isEditing = false;
        private int currEditingIdInList = -1;
        private int step = 0;
        short x0 = 0;
        short y0 = 0;

        public boolean place(int id) {
            if (id == 0) {
                currPlacingID = (short) id;
                isBusy = false;
                isEditing = false;
                step = 0;
            } else if (!isBusy) {
                mgStruct.updateHistory();
                isBusy = true;
                currPlacingID = (short) id;
                step = 1;
                currentPlacing = new short[mgStruct.args[currPlacingID] + 1];
                currentPlacing[0] = currPlacingID;
                return true;
            } else {
                int currID = currPlacingID;
                cancel();
                if (id != currID) {
                    place(id);
                } else {
                    selectedOption = -1;
                    selectedOptionInUpperRow = -1;
                }
            }
            return false;
        }

        public boolean place(int id, int x, int y) {
            if (id == 0) {
                currPlacingID = (short) id;
                isBusy = false;
                step = 0;
            } else if (!isBusy) {
                mgStruct.updateHistory();
                isBusy = true;
                currPlacingID = (short) id;
                step = 1;
                currentPlacing = new short[mgStruct.args[currPlacingID] + 1];
                currentPlacing[0] = currPlacingID;
                clicked(x, y);
                return true;
            } else {
                int currID = currPlacingID;
                cancel();
                if (id != currID) {
                    place(id, x, y);
                } else {
                    selectedOption = -1;
                    selectedOptionInUpperRow = -1;
                }
            }
            return false;
        }
        
        public boolean clicked(int x, int y) {
            if (suppressFirstClick) {
                suppressFirstClick = false;
                return false;
            }
            short xShort = (short) x;
            short yShort = (short) y;
            if (isBusy) {
                if (step >= mgStruct.clicks[currPlacingID]) {
                    selectedOption = -1;
                    selectedOptionInUpperRow = -1;
                    isBusy = false;
                    for (int i : currentPlacing) {
                        System.out.print(i + " ");
                    }
                    if (!isEditing) {
                        mgStruct.saveToBuffer(currentPlacing);
                        selectedInList = mgStruct.length - 1;
                    } else {
                        if (currEditingIdInList > -1) {
                            mgStruct.buffer[currEditingIdInList] = currentPlacing;
                        }
                    }
                    reloadList();
                    if (currPlacingID != 1)
                        calcEndPoint();
                    currPlacingID = 0;
                    isEditing = false;
                    wrongStartOfCurrPlacing = false;
                    checkStartPoint();
                    return true;
                }
                calcArgs(currPlacingID, step, xShort, yShort);
                if (isEditing) {
                    step = 3000;
                    clicked(x, y);
                }
                step++;
            }
            return false;
        }
        
        boolean wrongStartPointWarning = false;
        boolean wrongStartOfCurrPlacing = false;
        void calcArgs(short id, int step, short x, short y) {
            if (id == 1) {
                currentPlacing[1] = x;
                currentPlacing[2] = y;
            } else
            if (id == 2) {
                if (step == 1) {
                    currentPlacing[1] = x;
                    currentPlacing[2] = y;
                } else if (step == 2) {
                    currentPlacing[3] = x;
                    currentPlacing[4] = y;
                }
            } else
            if (id == 3) {
                if (step == 1) {
                    currentPlacing[1] = x;
                    currentPlacing[2] = y;
                }
                if (step == 2) {
                    x0 = currentPlacing[1];
                    y0 = currentPlacing[2];
                    short dx = (short) (x - x0);
                    short dy = (short) (y - y0);
                    //System.out.println(dx + " " + dy);
                    currentPlacing[3] = (short) Math.sqrt(dx * dx + dy * dy);
                    //System.out.println("r:" + currentPlacing[carriage]);
                    currentPlacing[4] = 360;
                    currentPlacing[5] = 0;
                    currentPlacing[6] = 100;
                    currentPlacing[7] = 100;
                }
            } else
            if (id == 4) {
                if (step == 1) {
                    currentPlacing[1] = x;
                    currentPlacing[2] = y;
                } else if (step == 2) {
                    currentPlacing[3] = x;
                    currentPlacing[4] = y;
                    currentPlacing[5] = 20;
                    int dx = currentPlacing[3] - currentPlacing[1];
                    int dy = currentPlacing[4] - currentPlacing[2];
                    int l;
                    int spacing = 10;
                    if (dy == 0) {
                        l = dx;
                    } else if (dx == 0) {
                        l = dy;
                    } else {
                        l = calcDistance(dx, dy);
                    }
                    l += spacing;
                    if (l <= 0) {
                        l = 1;
                    }
                    int optimalPlatfL = 130;
                    int platfL = optimalPlatfL;
                    if (platfL > l) {
                        platfL = l;
                    }
                    int platfL1 = platfL;
                    while (l%platfL != 0 & platfL < l & l%platfL1 != 0) {
                        platfL++;
                        platfL1--;
                    }
                    if (l%platfL == 0) {
                        platfL1 = platfL;
                    }
                    platfL = platfL1;
                    platfL -= spacing;
                    currentPlacing[6] = (short) platfL;
                    currentPlacing[7] = (short) spacing;
                    currentPlacing[8] = (short) (l - spacing);
                    currentPlacing[9] = (short) Math.toDegrees(Math.atan2(dy, dx));
                }
            } else
            if (id == 5) {
                if (step == 1) {
                    currentPlacing[1] = x;
                    currentPlacing[2] = y;
                }
                if (step == 2) {
                    x0 = currentPlacing[1];
                    y0 = currentPlacing[2];
                    //short dx = (short) (x - x0);
                    //short dy = (short) (y - y0);
                    //System.out.println(dx + " " + dy);
                    currentPlacing[3] = (short) calcDistance(x0, y0, x, y);
                    //System.out.println("r:" + currentPlacing[carriage]);
                    currentPlacing[4] = 360;
                    currentPlacing[5] = 0;
                    currentPlacing[6] = 100;
                    currentPlacing[7] = 100;
                    currentPlacing[8] = 20;
                }
            }

            short currCheckingX = currentPlacing[1];
            short currCheckingY = currentPlacing[2];
            if (id == 2 | id == 4) {
                short x2nd = currentPlacing[3];
                short y2nd = currentPlacing[4];
                if (compareAsStarts(currCheckingX, currCheckingY, x2nd, y2nd)) {
                    currCheckingX = x2nd;
                    currCheckingY = y2nd;
                }
            } else if (id == 3 | id == 5) {
                int r = currentPlacing[3];
                currCheckingX -= r;
            }
            wrongStartOfCurrPlacing = (currCheckingX != 0 | currCheckingY != 0) & mgStruct.length < 2;
            if (currCheckingX < 0) {
                wrongStartOfCurrPlacing = true;
            }
        }
        
        public void calcEndPoint() {
            short x = 0;
            short y = 0;
            for (int i = 1; i < mgStruct.length; i++) {
                short currCheckingX = mgStruct.buffer[i][1];
                short currCheckingY = mgStruct.buffer[i][2];
                int id = mgStruct.buffer[i][0];
                if (id == 2 | id == 4) {
                    short x2nd = mgStruct.buffer[i][3];
                    short y2nd = mgStruct.buffer[i][4];
                    if (compareAsEnds(currCheckingX, currCheckingY, x2nd, y2nd)) {
                        currCheckingX = x2nd;
                       currCheckingY = y2nd;
                    }
                } else if (id == 3 | id == 5) {
                    int r = mgStruct.buffer[i][3];
                    currCheckingX += r;
                }
                if (compareAsEnds(x, y, currCheckingX, currCheckingY)) {
                    x = currCheckingX;
                    y = currCheckingY;
                }
            }
            mgStruct.buffer[0][1] = x;
            mgStruct.buffer[0][2] = y;
        }
        private boolean compareAsEnds(short x, short y, short currCheckingX, short currCheckingY) {
            if (currCheckingX >= x) {
                if (currCheckingX > x | (currCheckingY > y)) {
                    x = currCheckingX;
                    y = currCheckingY;
                    return true;
                }
            }
            return false;
        }

        public void cancel() {
            place(0);
            repaint();
        }
        
        void delete(int i) {
            if (i == 0 | mgStruct.buffer[i][0] == 1) {
                return;
            }
            mgStruct.updateHistory();

            mgStruct.length--;
            for (int j = i; j < mgStruct.length; j++) {
                mgStruct.buffer[j] = mgStruct.buffer[j + 1];
            }
            selectedInList = mgStruct.length - 1;
            calcEndPoint();
            reloadList();
            repaint();
        }
        boolean suppressFirstClick = false;
        void edit(int idInList, int startWithStep) {
            mgStruct.updateHistory();
            currEditingIdInList = idInList;
            if (isBusy) {
                cancel();
            }
            int id = mgStruct.buffer[idInList][0];
            if (mgStruct.clicks[id] < startWithStep) {
                selectedOptionInUpperRow = -1;
                return;
            }
            isEditing = true;

            if (id == 0) {
                currPlacingID = (short) id;
                isBusy = false;
                step = 0;
                isEditing = false;
            } else if (!isBusy) {
                isBusy = true;
                currentPlacing = mgStruct.buffer[idInList];
                currPlacingID = (short) id;
                step = startWithStep;
                //return true;
            } else {
                int currID = currPlacingID;
                cancel();
                if (id != currID) {
                    edit(idInList, startWithStep);
                } else {
                    selectedOption = -1;
                    selectedOptionInUpperRow = -1;
                }
            }
            //suppressFirstClick = true;
            //return false;
        }

        void moveAllToStartPoint() {
            mgStruct.updateHistory();
            short[] dxdy = checkStartPoint();
            int dx = -dxdy[0];
            int dy = -dxdy[1];

            for (int i = 0; i < mgStruct.length; i++) {
                int id = mgStruct.buffer[i][0];
                mgStruct.buffer[i][1] += dx;
                mgStruct.buffer[i][2] += dy;
                if (id == 2 | id == 4) {
                    mgStruct.buffer[i][3] += dx;
                    mgStruct.buffer[i][4] += dy;
                }
            }
            selectedIn3Row = -1;
            wrongStartPointWarning = false;
        }

        public short[] checkStartPoint() {
            short x = 0;
            short y = 0;
            boolean first = true;
            for (int i = 1; i < mgStruct.length; i++) {
                short currCheckingX = mgStruct.buffer[i][1];
                short currCheckingY = mgStruct.buffer[i][2];
                if (first) {
                    x = currCheckingX;
                    y = currCheckingY;
                    first = false;
                }
                int id = mgStruct.buffer[i][0];
                if (id == 2 | id == 4) {
                    short x2nd = mgStruct.buffer[i][3];
                    short y2nd = mgStruct.buffer[i][4];
                    if (compareAsStarts(currCheckingX, currCheckingY, x2nd, y2nd)) {
                        currCheckingX = x2nd;
                        currCheckingY = y2nd;
                    }
                } else if (id == 3 | id == 5) {
                    int r = mgStruct.buffer[i][3];
                    currCheckingX -= r;
                }
                if (compareAsStarts(x, y, currCheckingX, currCheckingY)) {
                    x = currCheckingX;
                    y = currCheckingY;
                }
            }
            wrongStartPointWarning = (x != 0) | (y != 0);
            return new short[]{x, y};
        }
        private boolean compareAsStarts(short x, short y, short currCheckingX, short currCheckingY) {
            if (currCheckingX <= x) {
                if (currCheckingX < x | (currCheckingY < y)) {
                    return true;
                }
            }
            return false;
        }
        int calcDistance(int x1, int y1, int x2, int y2) {
            int dx = x2 - x1;
            int dy = y2 - y1;
            return calcDistance(dx, dy);
        }
        int calcDistance(int dx, int dy) {
            return (int) Math.sqrt(dx * dx + dy * dy);
        }
    }

    void init() {
        offsetX = getWidth() / 2;
        offsetY = getHeight() / 2;
        carX = 0 - (carbodyLength / 2 - wheelRadius);
        carY = 0 - wheelRadius / 2 * 3 - 2;
        elements.place(1, 0, 0);
    }
    
    void drawCar(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.drawRect(calcX(carX - carbodyLength / 2), calcY(carY - carbodyHeight / 2), carbodyLength * 1000 / zoomOut, carbodyHeight * 1000 / zoomOut);
        lwX = calcX(carX - (carbodyLength / 2 - wheelRadius));
        lwY = calcY(carY + wheelRadius / 2);
        rwX = calcX(carX + (carbodyLength / 2 - wheelRadius));
        rwY = calcY(carY + wheelRadius / 2);
        g.setColor(Color.black);
        int wheelRadius = this.wheelRadius * 1000 / zoomOut;
        g.fillArc(lwX - wheelRadius, lwY - wheelRadius, wheelRadius * 2, wheelRadius * 2, 0, 360);
        g.fillArc(rwX - wheelRadius, rwY - wheelRadius, wheelRadius * 2, wheelRadius * 2, 0, 360);
        g.setColor(Color.DARK_GRAY);
        g.drawArc(lwX - wheelRadius, lwY - wheelRadius, wheelRadius * 2, wheelRadius * 2, 0, 360);
        g.drawArc(rwX - wheelRadius, rwY - wheelRadius, wheelRadius * 2, wheelRadius * 2, 0, 360);
        int lineEndX = carX - carbodyLength / 2 - wheelRadius / 2;
        int lineStartX = lineEndX - wheelRadius;
        int lineY = carY + carbodyHeight / 3;
        g.drawLine(calcX(lineStartX), calcY(lineY), calcX(lineEndX), calcY(lineY));
        lineStartX += carbodyHeight / 3;
        lineEndX += carbodyHeight / 3;
        lineY += carbodyHeight / 3;
        g.drawLine(calcX(lineStartX), calcY(lineY), calcX(lineEndX), calcY(lineY));
        lineStartX -= carbodyHeight * 2 / 3;
        lineEndX -= carbodyHeight * 2 / 3;
        lineY -= carbodyHeight * 2 / 3;
        g.drawLine(calcX(lineStartX), calcY(lineY), calcX(lineEndX), calcY(lineY));
    }

    String getShapeName(int id) {
        return mgStruct.shapeNames[id];
    }

    /*void calcEndPoint() {
        short x = 0;
        short y = 0;
        for (int i = 1; i < mgStruct.length; i++) {
            short currCheckingX = mgStruct.buffer[i][1];
            short currCheckingY = mgStruct.buffer[i][2];
            if (mgStruct.buffer[i][0] == 2) {
                short x2nd = mgStruct.buffer[i][3];
                short y2nd = mgStruct.buffer[i][4];
                if (compareAsEnds(currCheckingX, currCheckingY, x2nd, y2nd)) {
                    currCheckingX = x2nd;
                    currCheckingY = y2nd;
                }
            }
            if (compareAsEnds(x, y, currCheckingX, currCheckingY)) {
                x = currCheckingX;
                y = currCheckingY;
            }
        }
        mgStruct.buffer[0][1] = x;
        mgStruct.buffer[0][2] = y;
    }
    boolean compareAsEnds(short x, short y, short currCheckingX, short currCheckingY) {
        if (currCheckingX >= x) {
            if (currCheckingX > x | (currCheckingY > y)) {
                x = currCheckingX;
                y = currCheckingY;
                return true;
            }
        }
        return false;
    }*/
    void reloadList() {
        mgStruct.changed = true;
    }
}
