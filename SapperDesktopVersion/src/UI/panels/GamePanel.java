package UI.panels;

import UI.SwingPaint;
import game.ActionField;
import game.Cell;
import game.IGameListener;
import game.solver.Solver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Created by Sergey on 10.07.2017.
 */

public class GamePanel extends JPanel implements IGameListener, IPanelTopListener {
    private FieldPainter fieldPainter;
    private ActionField actionField;
    private boolean isWin;
    private boolean gameFinished;
    private boolean isSetFlagMode = false;
    private GamePanelListener gamePanelListener;
    private Solver solver;
    public static final Font font = new Font("Calibri", Font.BOLD, 20);

    public GamePanel(ActionField newActionField, PanelTopListener topListener, GamePanelListener gpl, Solver solver) {
        this.gamePanelListener = gpl;
        this.solver = solver;
        topListener.addListener(this);
        createGame(newActionField);
        setBackground(Color.GRAY);
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (!gameFinished) {
                    int row = (e.getX() - fieldPainter.START_X) / FieldPainter.CELL_WIDTH;
                    int column = (e.getY() - fieldPainter.START_Y) / FieldPainter.CELL_HEIGHT;
                    if (SwingUtilities.isLeftMouseButton(e) && !isSetFlagMode) {
                        actionField.openCell(row, column);
                        if (!gameFinished) {
                            gamePanelListener.sayTouchField();
                        }
                    } else if (isSetFlagMode || SwingUtilities.isRightMouseButton(e)) {
                        actionField.putTagged(row, column);
                    }
                }
            }
        });
    }

    private void createGame(ActionField actionField) {
        actionField.addListener(this);
        this.actionField = actionField;
        gameFinished = false;
        isWin = false;
        isSetFlagMode = false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont( font );
        if (SwingPaint.activeSolver) {
            fieldPainter = new FieldPainter(actionField, isWin, gameFinished, solver.getField());
        } else {
            fieldPainter = new FieldPainter(actionField, isWin, gameFinished);
        }
        if (gameFinished) {
            fieldPainter.drawFinishGame(g);
        } else {
            fieldPainter.drawField(g);
        }
    }

    @Override
    public void cellsChanged(java.util.List<Cell> cells) {
        repaint();
    }

    @Override
    public void gameOver(boolean isWin) {
        this.isWin = isWin;
        gameFinished = true;
        repaint();
    }

    @Override
    public void scoreChange(final int score) { }

    @Override
    public void gameStart() { }

    @Override
    public void restartGame(ActionField newActionField) {
        createGame(newActionField);

        solver.recreate(newActionField);
        repaint();
    }

    @Override
    public void changeFlagMode(boolean isSetFlagMode) {
        this.isSetFlagMode = isSetFlagMode;
    }
}