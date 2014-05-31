package com.sokoban.game;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Level {
	public String pack_name  = "";     // Name of pack that contains this level
	public String level_name = "";     // Name of this level
	public String copyright  = "";     // Copyright of this level
	public int boxes_number  = 0;      // Number of boxes in this level
	public int goals_number  = 0;      // Number of goals in this level
	public int rows_number   = 0;      // Rows number
	public int cols_number   = 0;      // Cols number
	public int pusher_pos_m  = 0;      // M position of the pusher
	public int pusher_pos_n  = 0;      // N position of the pusher
	private ArrayList<Character> grid; // Grid of the level
	
  public Level(String line, int width, int height, String pack_name, String level_name, String copyright) {
	  this.pack_name   = pack_name;
	  this.level_name  = level_name;
	  this.copyright   = copyright;
	  this.rows_number = height;
	  this.cols_number = width;
	  this.grid = new ArrayList<Character>();

	  for(int i=0;i<height*width;i++) {
	    char c = line.charAt(i);
	    grid.add(Character.valueOf(c));
	  }
	  
	  // Find initial position of pusher
    initialize_pusher_position();
	  
	  // Make floor inside the level
    make_floor();

    // Initialize number of boxes and goals
    for(int i=0; i<height*width; i++) {
      char cell = grid.get(i);
      if(cell == '*' || cell == '$')
        boxes_number = boxes_number + 1;
      if(cell == '+' || cell == '*' || cell == '.')
        goals_number = goals_number + 1;
    }
	}
  
  // Read the value of position (m,n).
  // Position start in the upper-left corner of the grid with (0,0).
  // @param m Row number.
  // @param n Col number.
  // @return Value of position (m,n) or 'E' if pos is out of grid.
  public char read_pos(int m, int n) {
    if(m < rows_number && n < cols_number && m >= 0 && n >= 0)
      return grid.get(cols_number*m + n);
    else
      return 'E';
  }

  // Write the value of letter in position (m,n).
  // Position start in the upper-left corner of the grid with (0,0).
  // @param m Row number.
  // @param n Col number.
  // @param letter value to assign at (m,n) in the grid
  public void write_pos(int m, int n, char letter) {
    if(m < rows_number && n < cols_number && m >= 0 && n >= 0)
      grid.set(cols_number*m + n, letter);
  }
  
  // Return true if all boxes are in their goals.
  // @return true if all boxes are in their goals, false if not
  public boolean is_won() {
    for(int i=0; i< rows_number*cols_number; i++) {
      if(grid.get(i) == '$')
        return false;
    }
    return true;
  }
  
  // Print the level in the javascript console
  public void print() {
    for(int i=0; i< rows_number; i++) {
      String line = "";
      for(int j=0; j< cols_number; j++) {
        line = line + read_pos(i, j);
      }
      Log.e("Level", "Level: " + line + "\n");
    }
  }
  
  // Look if pusher can move in a given direction
  // @param direction 'u', 'd', 'l', 'r' in lowercase and uppercase
  // @return true if pusher can move in this direction, false if not.
  public boolean pusher_can_move(char direction) {
    char mouv1 = ' ';
    char mouv2 = ' ';
    int m = pusher_pos_m;
    int n = pusher_pos_n;

  // Following of the direction, test 2 cells
    if(direction == 'u') {
      mouv1 = read_pos(m-1, n);
      mouv2 = read_pos(m-2, n);
    }
    else if(direction == 'd') {
      mouv1 = read_pos(m+1, n);
      mouv2 = read_pos(m+2, n);
    }
    else if(direction == 'l') {
      mouv1 = read_pos(m, n-1);
      mouv2 = read_pos(m, n-2);
    }
    else if(direction == 'r') {
      mouv1 = read_pos(m, n+1);
      mouv2 = read_pos(m, n+2);
    }

    // If (there is a wall) OR (two boxes or one box and a wall)
    if(mouv1 == '#' || ((mouv1 == '*' || mouv1 == '$') && (mouv2 == '*' || mouv2 == '$' || mouv2 == '#')))
      return false;
    else
      return true;
  }
  
  // Move the pusher in a given direction and save it in the actualPath
  // @param direction Direction where to move the pusher (u,d,l,r,U,D,L,R)
  // @return 0 if no move.
  //         1 if normal move.
  //         2 if box push.
  public int move(char direction) {
    int action = 1;
    int m = pusher_pos_m;
    int n = pusher_pos_n;
    int m_1 = 0, m_2 = 0, n_1 = 0, n_2 = 0;
    int state = 0;

    // accept upper and lower dir
    direction = Character.toLowerCase(direction);

    // Following of the direction, test 2 cells
    if(direction == 'u' && pusher_can_move('u')) {
      m_1 = m-1;
      m_2 = m-2;
      n_1 = n_2 = n;
      pusher_pos_m--;
    }
    else if(direction == 'd' && pusher_can_move('d')) {
      m_1 = m+1;
      m_2 = m+2;
      n_1 = n_2 = n;
      pusher_pos_m++;
    }
    else if(direction == 'l' && pusher_can_move('l')) {
      n_1 = n-1;
      n_2 = n-2;
      m_1 = m_2 = m;
      pusher_pos_n--;
    }
    else if(direction == 'r' && pusher_can_move('r')) {
      n_1 = n+1;
      n_2 = n+2;
      m_1 = m_2 = m;
      pusher_pos_n++;
    }
    else {
      action = 0;
      state = 0;
    }

    // Move accepted
    if(action == 1) {
      state = 1;

      // Test on cell (m,n)
      if(read_pos(m, n) == '+')
        write_pos(m, n, '.');
      else
        write_pos(m, n, 's');

      // Test on cell (m_2,n_2)
      if(read_pos(m_1, n_1) == '$' || read_pos(m_1, n_1) == '*') {
        if(read_pos(m_2, n_2) == '.')
          write_pos(m_2, n_2, '*');
        else
          write_pos(m_2, n_2, '$');

        state = 2;
      }

      // Test on cell (m_1, n_1)
      if(read_pos(m_1, n_1) == '.' || read_pos(m_1, n_1)=='*')
        write_pos(m_1, n_1, '+');
      else
        write_pos(m_1, n_1, '@');
    }

    return state;
  }
  
  // Initialize (find) starting position of pusher to store it in this object
  private void initialize_pusher_position() {
    boolean find = false;

    for(int i=0; i< rows_number*cols_number; i++) {
      char cell = grid.get(i);
      if(!find && (cell == '@' || cell == '+')) {
         pusher_pos_n = i % cols_number;
         pusher_pos_m = i / cols_number;
         find = true;
      }
    }
  }
  
  // Transform empty spaces inside level in floor represented by 's' used
  // to draw the level. Call to recursive function "makeFloorRec".
  private void make_floor() {
    // Recursively set "inside floor" to 's' starting with pusher position
    make_floor_rec(pusher_pos_m, pusher_pos_n);

    // Set back modified (by recusively method) symbols to regular symbols
    for(int i=0; i< rows_number*cols_number; i++) {
      char cell = grid.get(i);
      if(cell == 'p')
        grid.set(i, '.');
      else if(cell == 'd')
        grid.set(i, '$');
      else if(cell == 'a')
        grid.set(i, '*');
    }
  }
  
  // Recursive function used to transform inside spaces by floor ('s')
  // started with initial position of sokoban.
  // NEVER use this function directly. Use make_floor instead.
  // @param m Rows number (start with sokoban position)
  // @param n Cols number (start with sokoban position)
  private void make_floor_rec(int m, int n) {
    char a = read_pos(m, n);
    
    // Change of values to "floor" or "visited"
    if(a == ' ')
      write_pos(m, n, 's');
    else if(a == '.')
      write_pos(m, n, 'p');
    else if(a == '$')
      write_pos(m, n, 'd');
    else if(a == '*')
      write_pos(m, n, 'a');
    
    // If non-visited cell, test neighbours cells
    if(a != '#' && a != 's' && a != 'p' && a == 'd' && a != 'a') {
      make_floor_rec(m+1, n);
      make_floor_rec(m-1, n);
      make_floor_rec(m, n+1);
      make_floor_rec(m, n-1);
    }
  }
}
