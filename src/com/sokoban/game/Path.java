/*
  Class usefull to represent a way to move in a level.
  
  Pushes are represented by upper case and moves by lower case
  It could be written in COMPRESSED mode, but in this class, it's
  UNCOMPRESSED for rapidity and flexibility.
  
  In compressed mode, when many moves in one direction, letters are prefixed
  by numbers.
  
  example uncompressed mode : rrrllUUUUUruLLLdlluRRRRdr
  example compressed mode : 3r2l5Uru3Ld2lu4Rdr
*/


package com.sokoban.game;

import java.util.ArrayList;

import android.util.Log;

public class Path {
  public int n_pushes;
  public int n_moves;
  public ArrayList<Character> moves;
  
  public Path() {
    n_pushes = 0;
    n_moves  = 0;
    moves = new ArrayList<Character>();
  }
  
  // Constructor from an uncompressed path
  // @param uncompressed_path (string)
  public void create_from_uncompressed(ArrayList<Character> uncompressed_path) {
    n_pushes = 0;
    n_moves  = 0;
    moves = new ArrayList<Character>();
    
    for(int i=0; i<uncompressed_path.size(); i++) {
      add_displacement(uncompressed_path.get(i));
    }
  }
  
  // Constructor from a compressed path
  // @param compressed_path (string)
  public void create_from_compressed(ArrayList<Character> compressed_path) {
    n_pushes = 0;
    n_moves  = 0;
    moves                             = new ArrayList<Character>();
    ArrayList<Character> cmpr_moves   = new ArrayList<Character>();
    ArrayList<Character> uncmpr_moves = new ArrayList<Character>();
        
    for(int i=0; i<compressed_path.size(); i++) {
      cmpr_moves.add(compressed_path.get(i));
    }
    uncmpr_moves = uncompress_path(cmpr_moves);
    
    for(int i=0; i<uncmpr_moves.size(); i++) {
      add_displacement(uncmpr_moves.get(i));
    }
  }

  // Add move in a direction
  // @param direction direction where the pusher move
  public void add_move(char direction) {
    if(is_valid_direction(direction)) {
      n_moves++;
      moves.add(Character.toLowerCase(direction));
    }
  }

  // Add push in a direction
  // @param direction direction where pusher push a box
  public void add_push(char direction) {
    if(is_valid_direction(direction)) {
      n_moves++;
      n_pushes++;
      moves.add(Character.toUpperCase(direction));
    }
  }
  
  // Add displacement in a direction.
  // push or move is automatically assigned following of the letter-case of direction
  // @param direction direction where pusher push a box
  public void add_displacement(char direction) {
    if(is_valid_direction(direction)) {
      if(direction >= 'A' && direction <= 'Z') {
        n_pushes++;
      }
      n_moves++;
      moves.add(direction);
    }
  }
  
  // Get letter of last action (can be move or push)
  // @return letter or false if not last move
  public char get_last_move() {
    if(moves.size() > 0)
      return moves.get(moves.size() - 1);
    else
      return ' ';
  }
    
  // Delete last move or push
  public void delete_last_move() {
    if(n_moves > 0) {
      char last_move = moves.remove(moves.size()-1);
      if(last_move >= 'A' && last_move <= 'Z')
        n_pushes--;
      n_moves--;
    }
  }

  // print path
  public void print() {
    print_uncompressed();
  }

  // Print uncompressed path of this object
  public void print_uncompressed() {
    String line = "";
    
    for(int i=0; i<moves.size(); i++) {
      line = line + moves.get(i);
    }

    Log.e("Level", "Level path:" + line);
  }
  
  // Print compressed path of this object
  public void print_compressed() {
    ArrayList<Character> compressed_moves = compress_path(moves);
    String line = "";
    
    for(int i=0; i<compressed_moves.size(); i++) {
      line = line + compressed_moves.get(i);
    }
    
    Log.e("Level", "Level path:" + line);
  }
  
  // Is a direction valid (u,d,r,l,U,D,R,L)
  // @param direction direction to test
  // @return true if valid, false if not
  boolean is_valid_direction(char direction) {
    char d = Character.toLowerCase(direction);
    return (d == 'u' || d == 'd' || d == 'l' || d == 'r');
  }
  
  // Compress a path (see description of class)
  // @param path uncompressed path
  // @return compressed path (array)
  public ArrayList<Character> compress_path(ArrayList<Character> uncompressed_path) {
    ArrayList<Character> compressed_path = new ArrayList<Character>();

    // We loop on each cell of the uncompressed path
    int length = uncompressed_path.size();
    int i = 0;
    while(i < length) {
      int j = 0;
      char tabi = uncompressed_path.get(i);
    
      // while it's the same displacement and we're not in the end
      while(tabi == uncompressed_path.get(i) && i < length) {
        // (Identical moves)++
        j = j + 1;
        i = i + 1;
      }

      // If there are many moves in one direction 
      if(j >= 2)
        for(int k=0; k<String.valueOf(j).length(); k++)
          compressed_path.add(String.valueOf(j).substring(k, k+1).charAt(0));

      // In every case, add direction and '\0' next to it
      compressed_path.add(tabi);
    }

    return compressed_path;
  }
  
  // Uncompress a path (see description of Path class)
  // @param path compressed path
  // @return uncompressed path (array)
  public ArrayList<Character> uncompress_path(ArrayList<Character> compressed_path) {
    int i = 0;
    ArrayList<Character> uncompressed_path = new ArrayList<Character>();
  
    // While we're not in the end of compressed path
    while(i < compressed_path.size()) {
      char cell = compressed_path.get(i);
      ArrayList<Character> nbr_buffer = new ArrayList<Character>();
    
      // if not decimal, only 1 move/push
      if(cell < '0' || cell > '9')
        nbr_buffer.add('1');
  
      // if decimal, we put it in buffer to create a number like ['1', '2'] for 12
      while(cell >= '0' && cell <= '9') {
        nbr_buffer.add(cell);
        i = i + 1;
        cell = compressed_path.get(i);
      }
  
      // we transform the array on string...
      String nbr_string = "";
      for(int j=0; j<nbr_buffer.size(); j++)
        nbr_string = nbr_string + nbr_buffer.get(j);

      // ... and then convert the string on integer
      int nbr = Integer.parseInt(nbr_string);
  
      // write nbr times the move/push
      for(int j=0; j<nbr; j++)
        uncompressed_path.add(compressed_path.get(i));
  
      i = i + 1;
    }
  
    return uncompressed_path;
  }
}

