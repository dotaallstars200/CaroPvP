package com.edu.xogame.players;

import com.edu.xogame.datastructure.CellPosition;
import com.edu.xogame.views.Board;

public class PlayerBot extends Player {

    private final Board board;
    private static final int DEPTH = 1;
    private final CellPosition DEFAULT_MOVE = new CellPosition(5, 5);

    public PlayerBot(Board board) {
        super(board);
        this.board = board;
    }

    private CellPosition thinkMoves() {
        int maxPoint = 0;
        int defensePoint = 0;
        int attackPoint = 0;
        if (board.getCheckedCells().isEmpty()) {
            return DEFAULT_MOVE;
        }
        // player goes first

        CellPosition move = null;
        for (int i = 0; i < Board.NUMBER_ROWS; i++) {
            for (int j = 0; j < Board.NUMBER_COLUMNS; j++) {
                //nếu nước cờ chưa có ai đánh và không bị cắt tỉa thì mới xét giá trị MinMax
                if (getBoard().getTrackTable()[i][j] == 0 && !prune(new CellPosition(i, j)))
                {
                    int decisivePoint;

                    attackPoint = traverseHorizontalAttacking(i, j) + traverseVerticalAttacking(i, j) + traverseMainDiagonalAttacking(i, j) + traverseAntiDiagonalAttacking(i, j);
                    defensePoint = traverseHorizontalDefence(i, j) + traverseVerticalDefence(i, j) + traverseMainDiagonalDefence(i, j) + traverseAntiDiagonalDefence(i, j);

                    if (defensePoint > attackPoint) {
                        decisivePoint = defensePoint;
                    } else {
                        decisivePoint = attackPoint;
                    }

                    if (maxPoint < decisivePoint) {
                        maxPoint = decisivePoint;
                        move = new CellPosition(i, j);
                    }
                }
            }
        }

        return move;
    }

    public void checkCell() {
        CellPosition cellPosition = thinkMoves();
        board.checkCell(board.getCell(cellPosition));
    }

    public Board getBoard() {
        return board;
    }


    // Cắt tỉa Alpha beta
    boolean prune(CellPosition cell) {
        //nếu cả 4 hướng đều không có nước cờ thì cắt tỉa
        return horizontalPruning(cell) && verticalPruning(cell) && rightDiagonalPruning(cell) && leftDiagonalPruning(cell);

        //chạy đến đây thì 1 trong 4 hướng vẫn có nước cờ thì không được cắt tỉa
    }

    boolean horizontalPruning(CellPosition cellPos) {
        //duyệt bên phải
        if (cellPos.column <= Board.NUMBER_COLUMNS - 5)
            for (int i = 1; i <= 4; i++)
                if (getBoard().getTrackTable()[cellPos.row][cellPos.column + i] != 0)//nếu có nước cờ thì không cắt tỉa
                    return false;

        //duyệt bên trái
        if (cellPos.column >= 4)
            for (int i = 1; i <= 4; i++)
                if (getBoard().getTrackTable()[cellPos.row][cellPos.column - i] != 0)//nếu có nước cờ thì không cắt tỉa
                    return false;

        //nếu chạy đến đây tức duyệt 2 bên đều không có nước đánh thì cắt tỉa
        return true;
    }

    boolean verticalPruning(CellPosition cellPos) {
        //duyệt phía dưới
        if (cellPos.row <= Board.NUMBER_ROWS - 5)
            for (int i = 1; i <= 4; i++)
                if (getBoard().getTrackTable()[cellPos.row + i][cellPos.column] != 0)//nếu có nước cờ thì không cắt tỉa
                    return false;

        //duyệt phía trên
        if (cellPos.row >= 4)
            for (int i = 1; i <= 4; i++)
                if (getBoard().getTrackTable()[cellPos.row - i][cellPos.column] != 0)//nếu có nước cờ thì không cắt tỉa
                    return false;

        //nếu chạy đến đây tức duyệt 2 bên đều không có nước đánh thì cắt tỉa
        return true;
    }

    boolean rightDiagonalPruning(CellPosition cellPos) {
        //duyệt từ trên xuống
        if (cellPos.row <= Board.NUMBER_ROWS - 5 && cellPos.column >= 4)
            for (int i = 1; i <= 4; i++)
                if (getBoard().getTrackTable()[cellPos.row + i][cellPos.column - i] != 0)//nếu có nước cờ thì không cắt tỉa
                    return false;

        //duyệt từ dưới lên
        if (cellPos.column <= Board.NUMBER_COLUMNS - 5 && cellPos.row >= 4)
            for (int i = 1; i <= 4; i++)
                if (getBoard().getTrackTable()[cellPos.row - i][cellPos.column + i] != 0)//nếu có nước cờ thì không cắt tỉa
                    return false;

        //nếu chạy đến đây tức duyệt 2 bên đều không có nước đánh thì cắt tỉa
        return true;
    }

    boolean leftDiagonalPruning(CellPosition cellPos) {
        //duyệt từ trên xuống
        if (cellPos.row <= Board.NUMBER_ROWS - 5 && cellPos.column <= Board.NUMBER_COLUMNS - 5)
            for (int i = 1; i <= 4; i++)
                if (getBoard().getTrackTable()[cellPos.row + i][cellPos.column + i] != 0)//nếu có nước cờ thì không cắt tỉa
                    return false;

        //duyệt từ dưới lên
        if (cellPos.column >= 4 && cellPos.row >= 4)
            for (int i = 1; i <= 4; i++)
                if (getBoard().getTrackTable()[cellPos.row - i][cellPos.column - i] != 0)//nếu có nước cờ thì không cắt tỉa
                    return false;

        //nếu chạy đến đây tức duyệt 2 bên đều không có nước đánh thì cắt tỉa
        return true;
    }

    // AI

    private static final int[] ATTACK_POINTS = new int[]{0, 4, 25, 246, 7300, 6561, 59049};
    private static final int[] DEFENSE_POINTS = new int[]{0, 3, 24, 243, 2197, 19773, 177957};

    // Tấn công
    //duyệt ngang
    public int traverseHorizontalAttacking(int row, int col) {
        int attackPoint = 0;
        int ourCells = 0;
        int opponentsOnRight = 0;
        int opponentsOnLeft = 0;
        int numberGaps = 0;

        //bên phải
        for (int count = 1; count <= 4 && col < Board.NUMBER_COLUMNS - 5; count++) {

            if (board.getTrackTable()[row][col + count] == 1) {
                if (count == 1)
                    attackPoint += 37;

                ourCells++;
                numberGaps++;
            } else if (board.getTrackTable()[row][col + count] == -1) {
                opponentsOnRight++;
                break;
            } else numberGaps++;
        }
        //bên trái
        for (int count = 1; count <= 4 && col > 4; count++) {
            if (getBoard().getTrackTable()[row][col - count] == 1) {
                if (count == 1)
                    attackPoint += 37;

                ourCells++;
                numberGaps++;
            } else if (getBoard().getTrackTable()[row][col - count] == -1) {
                opponentsOnLeft++;
                break;
            } else numberGaps++;
        }
        //bị chặn 2 đầu khoảng chống không đủ tạo thành 5 nước
        if (opponentsOnRight > 0 && opponentsOnLeft > 0 && numberGaps < 4)
            return 0;

        attackPoint -= DEFENSE_POINTS[opponentsOnRight + opponentsOnLeft];
        attackPoint += ATTACK_POINTS[ourCells];
        return attackPoint;
    }

    //duyệt dọc
    public int traverseVerticalAttacking(int row, int col) {
        int attackPoint = 0;
        int ourCells = 0;
        int opponentsOnTop = 0;
        int opponentsOnBot = 0;
        int numberGaps = 0;

        //bên trên
        for (int dem = 1; dem <= 4 && row > 4; dem++) {
            if (getBoard().getTrackTable()[row - dem][col] == 1) {
                if (dem == 1)
                    attackPoint += 37;

                ourCells++;
                numberGaps++;

            } else if (getBoard().getTrackTable()[row - dem][col] == -1) {
                opponentsOnTop++;
                break;
            } else numberGaps++;
        }
        //bên dưới
        for (int dem = 1; dem <= 4 && row < Board.NUMBER_ROWS - 5; dem++) {
            if (getBoard().getTrackTable()[row + dem][col] == 1) {
                if (dem == 1)
                    attackPoint += 37;

                ourCells++;
                numberGaps++;

            } else if (getBoard().getTrackTable()[row + dem][col] == -1) {
                opponentsOnBot++;
                break;
            } else numberGaps++;
        }
        //bị chặn 2 đầu khoảng chống không đủ tạo thành 5 nước
        if (opponentsOnTop > 0 && opponentsOnBot > 0 && numberGaps < 4)
            return 0;

        attackPoint -= DEFENSE_POINTS[opponentsOnTop + opponentsOnBot];
        attackPoint += ATTACK_POINTS[ourCells];
        return attackPoint;
    }

    //chéo xuôi
    public int traverseMainDiagonalAttacking(int row, int col) {
        int attackPoint = 1;
        int ourCells = 0;
        int opponentOnHalfBot = 0;
        int opponentOnHalfTop = 0;
        int numberGaps = 0;

        //bên chéo xuôi xuống
        for (int count = 1; count <= 4 && col < Board.NUMBER_COLUMNS - 5 && row < Board.NUMBER_ROWS - 5; count++) {
            if (getBoard().getTrackTable()[row + count][col + count] == 1) {
                if (count == 1)
                    attackPoint += 37;

                ourCells++;
                numberGaps++;

            } else if (getBoard().getTrackTable()[row + count][col + count] == -1) {
                opponentOnHalfBot++;
                break;
            } else numberGaps++;
        }
        //chéo xuôi lên
        for (int count = 1; count <= 4 && row > 4 && col > 4; count++) {
            if (getBoard().getTrackTable()[row - count][col - count] == 1) {
                if (count == 1)
                    attackPoint += 37;

                ourCells++;
                numberGaps++;

            } else if (getBoard().getTrackTable()[row - count][col - count] == -1) {
                opponentOnHalfTop++;
                break;
            } else numberGaps++;
        }
        //bị chặn 2 đầu khoảng chống không đủ tạo thành 5 nước
        if (opponentOnHalfBot > 0 && opponentOnHalfTop > 0 && numberGaps < 4)
            return 0;

        attackPoint -= DEFENSE_POINTS[opponentOnHalfBot + opponentOnHalfTop];
        attackPoint += ATTACK_POINTS[ourCells];
        return attackPoint;
    }

    //chéo ngược
    public int traverseAntiDiagonalAttacking(int row, int col) {
        int attackPoint = 0;
        int ourCells = 0;
        int opponentOnHalfTop = 0;
        int opponentOnHalfBot = 0;
        int numberGaps = 0;

        //chéo ngược lên
        for (int count = 1; count <= 4 && col < Board.NUMBER_COLUMNS - 5 && row > 4; count++) {
            if (getBoard().getTrackTable()[row - count][col + count] == 1) {
                if (count == 1)
                    attackPoint += 37;

                ourCells++;
                numberGaps++;

            } else if (getBoard().getTrackTable()[row - count][col + count] == -1) {
                opponentOnHalfTop++;
                break;
            } else numberGaps++;
        }
        //chéo ngược xuống
        for (int count = 1; count <= 4 && col > 4 && row < Board.NUMBER_COLUMNS - 5; count++) {
            if (getBoard().getTrackTable()[row + count][col - count] == 1) {
                if (count == 1)
                    attackPoint += 37;

                ourCells++;
                numberGaps++;

            } else if (getBoard().getTrackTable()[row + count][col - count] == -1) {
                opponentOnHalfBot++;
                break;
            } else numberGaps++;
        }
        //bị chặn 2 đầu khoảng chống không đủ tạo thành 5 nước
        if (opponentOnHalfTop > 0 && opponentOnHalfBot > 0 && numberGaps < 4)
            return 0;

        attackPoint -= DEFENSE_POINTS[opponentOnHalfTop + opponentOnHalfBot];
        attackPoint += ATTACK_POINTS[ourCells];
        return attackPoint;
    }

    // phòng ngự

    //duyệt ngang
    public int traverseHorizontalDefence(int row, int col) {
        int defensePoint = 0;
        int ourCellsLeft = 0;
        int ourCellsRight = 0;
        int opponentCells = 0;
        int numberGapsRight = 0;
        int numberGapsLeft = 0;
        boolean ok = false;


        for (int count = 1; count <= 4 && col < Board.NUMBER_COLUMNS - 5; count++) {
            if (getBoard().getTrackTable()[row][col + count] == -1) {
                if (count == 1)
                    defensePoint += 9;

                opponentCells++;
            } else if (getBoard().getTrackTable()[row][col + count] == 1) {
                if (count == 4)
                    defensePoint -= 170;

                ourCellsLeft++;
                break;
            } else {
                if (count == 1)
                    ok = true;

                numberGapsRight++;
            }
        }

        if (opponentCells == 3 && numberGapsRight == 1 && ok)
            defensePoint -= 200;

        ok = false;

        for (int count = 1; count <= 4 && col > 4; count++) {
            if (getBoard().getTrackTable()[row][col - count] == -1) {
                if (count == 1)
                    defensePoint += 9;

                opponentCells++;
            } else if (getBoard().getTrackTable()[row][col - count] == 1) {
                if (count == 4)
                    defensePoint -= 170;

                ourCellsRight++;
                break;
            } else {
                if (count == 1)
                    ok = true;

                numberGapsLeft++;
            }
        }

        if (opponentCells == 3 && numberGapsLeft == 1 && ok)
            defensePoint -= 200;

        if (ourCellsRight > 0 && ourCellsLeft > 0 && (numberGapsLeft + numberGapsRight + opponentCells) < 4)
            return 0;

        defensePoint -= ATTACK_POINTS[ourCellsRight + ourCellsRight];
        defensePoint += DEFENSE_POINTS[opponentCells];

        return defensePoint;
    }

    //duyệt dọc
    public int traverseVerticalDefence(int row, int col) {
        int defensePoint = 0;
        int ourCellsLeft = 0;
        int ourCellsRight = 0;
        int opponentCells = 0;
        int numberGapsTop = 0;
        int numberGapsBot = 0;
        boolean ok = false;

        //lên
        for (int count = 1; count <= 4 && row > 4; count++) {
            if (getBoard().getTrackTable()[row - count][col] == -1) {
                if (count == 1)
                    defensePoint += 9;

                opponentCells++;

            } else if (getBoard().getTrackTable()[row - count][col] == 1) {
                if (count == 4)
                    defensePoint -= 170;

                ourCellsRight++;
                break;
            } else {
                if (count == 1)
                    ok = true;

                numberGapsTop++;
            }
        }

        if (opponentCells == 3 && numberGapsTop == 1 && ok)
            defensePoint -= 200;

        ok = false;
        //xuống
        for (int count = 1; count <= 4 && row < Board.NUMBER_COLUMNS - 5; count++) {
            //gặp quân địch
            if (getBoard().getTrackTable()[row + count][col] == -1) {
                if (count == 1)
                    defensePoint += 9;

                opponentCells++;
            } else if (getBoard().getTrackTable()[row + count][col] == 1) {
                if (count == 4)
                    defensePoint -= 170;

                ourCellsLeft++;
                break;
            } else {
                if (count == 1)
                    ok = true;

                numberGapsBot++;
            }
        }

        if (opponentCells == 3 && numberGapsBot == 1 && ok)
            defensePoint -= 200;

        if (ourCellsRight > 0 && ourCellsLeft > 0 && (numberGapsTop + numberGapsBot + opponentCells) < 4)
            return 0;

        defensePoint -= ATTACK_POINTS[ourCellsLeft + ourCellsRight];
        defensePoint += DEFENSE_POINTS[opponentCells];
        return defensePoint;
    }

    //chéo xuôi
    public int traverseMainDiagonalDefence(int row, int col) {
        int defensePoint = 0;
        int ourCellsLeft = 0;
        int ourCellsRight = 0;
        int opponentCells = 0;
        int numberGapsTop = 0;
        int numberGapsBot = 0;
        boolean ok = false;

        //lên
        for (int count = 1; count <= 4 && row < Board.NUMBER_ROWS - 5 && col < Board.NUMBER_COLUMNS - 5; count++) {
            if (getBoard().getTrackTable()[row + count][col + count] == -1) {
                if (count == 1)
                    defensePoint += 9;

                opponentCells++;
            } else if (getBoard().getTrackTable()[row + count][col + count] == 1) {
                if (count == 4)
                    defensePoint -= 170;

                ourCellsRight++;
                break;
            } else {
                if (count == 1)
                    ok = true;

                numberGapsTop++;
            }
        }

        if (opponentCells == 3 && numberGapsTop == 1 && ok)
            defensePoint -= 200;

        ok = false;
        //xuống
        for (int count = 1; count <= 4 && row > 4 && col > 4; count++) {
            if (getBoard().getTrackTable()[row - count][col - count] == -1) {
                if (count == 1)
                    defensePoint += 9;

                opponentCells++;
            } else if (getBoard().getTrackTable()[row - count][col - count] == 1) {
                if (count == 4)
                    defensePoint -= 170;

                ourCellsLeft++;
                break;
            } else {
                if (count == 1)
                    ok = true;

                numberGapsBot++;
            }
        }

        if (opponentCells == 3 && numberGapsBot == 1 && ok)
            defensePoint -= 200;

        if (ourCellsRight > 0 && ourCellsLeft > 0 && (numberGapsTop + numberGapsBot + opponentCells) < 4)
            return 0;

        defensePoint -= ATTACK_POINTS[ourCellsRight + ourCellsLeft];
        defensePoint += DEFENSE_POINTS[opponentCells];

        return defensePoint;
    }

    //chéo ngược
    public int traverseAntiDiagonalDefence(int row, int col) {
        int defensePoint = 0;
        int ourCellsLeft = 0;
        int ourCellsRight = 0;
        int opponentCells = 0;
        int numberGapsTop = 0;
        int numberGapsBot = 0;
        boolean ok = false;

        //lên
        for (int count = 1; count <= 4 && row > 4 && col < Board.NUMBER_COLUMNS - 5; count++) {

            if (getBoard().getTrackTable()[row - count][col + count] == -1) {
                if (count == 1)
                    defensePoint += 9;

                opponentCells++;
            } else if (getBoard().getTrackTable()[row - count][col + count] == 1) {
                if (count == 4)
                    defensePoint -= 170;

                ourCellsRight++;
                break;
            } else {
                if (count == 1)
                    ok = true;

                numberGapsTop++;
            }
        }


        if (opponentCells == 3 && numberGapsTop == 1 && ok)
            defensePoint -= 200;

        ok = false;

        //xuống
        for (int count = 1; count <= 4 && row < Board.NUMBER_ROWS - 5 && col > 4; count++) {
            if (getBoard().getTrackTable()[row + count][col - count] == -1) {
                if (count == 1)
                    defensePoint += 9;

                opponentCells++;
            } else if (getBoard().getTrackTable()[row + count][col - count] == 1) {
                if (count == 4)
                    defensePoint -= 170;

                ourCellsLeft++;
                break;
            } else {
                if (count == 1)
                    ok = true;

                numberGapsBot++;
            }
        }

        if (opponentCells == 3 && numberGapsBot == 1 && ok)
            defensePoint -= 200;

        if (ourCellsRight > 0 && ourCellsLeft > 0 && (numberGapsTop + numberGapsBot + opponentCells) < 4)
            return 0;

        defensePoint -= ATTACK_POINTS[ourCellsLeft + ourCellsRight];
        defensePoint += DEFENSE_POINTS[opponentCells];

        return defensePoint;
    }


}
