package game;

import tests.CellInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.shuffle;

/**
 * Created by Sergey on 07.07.2017.
 */

public class Field {
    private final int counterBomb;
    protected Cell[][] field;
    protected final int row, column;
    protected boolean fieldCreated = false;
    private List<Cell> indexCells = new ArrayList<>();

    public Field(final int row, final int column, final int counterBomb) {
        assert row * column > counterBomb : "too many bombs";
        this.counterBomb = counterBomb;
        this.row = row;
        this.column = column;
        createEmptyField();
    }

    public Cell getCell(final int row, final int column) { return field[row][column]; }
    public int getRow() { return row; }
    public int getColumn() { return column; }
    public Cell[][] getField() { return field; }
    public int getCounterBomb() { return counterBomb; }
    public int getNumber(final int row, final int column) { return field[row][column].getNumber(); }
    public boolean isBomb(final int row, final int column) { return field[row][column].isBomb(); }
    public boolean isEmpty(final int row, final int column) { return field[row][column].isEmptyPoint(); }


    public boolean isOutOfBounds(final int row, final int column) {
        return row < 0 || column < 0 ||
                row >= field.length ||
                column >= field[0].length;
    }

    public void setTagged(final int row, final int column) {
        field[row][column].isOpened = true;
        field[row][column].isTagged = true;
    }

    public void removeTagged(final int row, final int column) {
        field[row][column].isOpened = false;
        field[row][column].isTagged = false;
    }

    private void incPoint(final int row, final int column) {
        if (isOutOfBounds(row, column) || isBomb(row, column)) return;
        field[row][column].incNumber();
    }

    private void incPoints(final int row, final int column) {
        for (int rowIndex = -1; rowIndex <= 1; rowIndex++) {
            for (int columnIndex = -1; columnIndex <= 1; columnIndex++) {
                if (rowIndex != 0 || columnIndex != 0) {
                    incPoint(row + rowIndex, column + columnIndex);
                }
            }
        }
    }

    protected Cell getBombLocation(int bombIndex) {
        return indexCells.get(bombIndex);
    }

    private void setBombs(int counterBomb) {
        while (counterBomb != 0) {
            Cell pr = getBombLocation(counterBomb - 1);
            int i = pr.row;
            int j = pr.column;
            field[i][j].setBomb();
            incPoints(i, j);
            counterBomb--;
        }
    }

    private void createEmptyField() {
        field = new Cell[row][column];
        for (int i = 0; i != row; i++) {
            for (int j = 0; j != column; j++) {
                Cell c = new Cell(i, j);
                field[i][j] = c;
                indexCells.add(c);
            }
        }
    }

    protected void createField() {
        assert !fieldCreated : "double field creation";
        fieldCreated = true;
        shuffle(indexCells);
        setBombs(counterBomb);
        indexCells.clear();
    }

    protected void createField(HashSet<Cell> startCells) {
        for (int i = indexCells.size() - 1; i >= 0; i--) {
            Cell cell = indexCells.get(i);
            if (startCells.contains(cell)) {
                indexCells.remove(i);
            }
        }
        createField();
    }
}