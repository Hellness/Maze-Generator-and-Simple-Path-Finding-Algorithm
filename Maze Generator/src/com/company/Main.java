package com.company;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Maze Size:");
        int size = scanner.nextInt();
        int sizeSqr = (int) Math.pow(size, 2);
        char wall = '\u2588';   // y,x
        char[][] maze = new char[2 * size + 1][2 * size + 1];
        int[] elementsNumber = new int[sizeSqr];
        Arrays.fill(elementsNumber, 1);
        int[][] elements = new int[sizeSqr][sizeSqr];
        initMaze(maze, wall, elements, sizeSqr);
        kruskalLikeAlgorithm(maze, size, sizeSqr, elements, elementsNumber);
        openEntrances(maze,size);
        int[] entryPoints = {0,1};
        printMaze(maze);
        PathFind.findAPath(maze,entryPoints);
        printMaze(maze);
    }
    public static void initMaze(char[][] maze, char wall, int[][] elements, int sizeSqr) {
        for (char[] chars : maze) {
            Arrays.fill(chars, ' ');
        }
        for (int i = 0; i < maze.length; i += 2) {
            for (int j = 0; j < maze.length; j++) {
                maze[i][j] = wall;
                maze[j][i] = wall;
            }
        }
        for (int[] element : elements) {
            Arrays.fill(element, -1);
        }
        for (int i = 0; i < sizeSqr; i++) {
            elements[i][0] = i + 1;
        }
    }
    public static void kruskalLikeAlgorithm(char[][] maze, int size, int sizeSqr,int[][] elements,int[] elementsNumber){
        int groupCounter = sizeSqr;
        while (elementsNumber[0] != sizeSqr) {
            int[] link = new int[3];
            int group = 0;
            choose(elements, elementsNumber, group, size, link, groupCounter, sizeSqr);
            connect(maze, link[0], link[1], group, link[2], elements, elementsNumber, size);
            groupCounter--;
            tidy(elements, elementsNumber, groupCounter, sizeSqr);
        }
    }
    public static void tidy(int[][] elements,int[] elementsNumber,int groupCounter,int sizeSqr){
        for(int i=0;i< groupCounter;i++){
            if(elements[i][0]==-1){
                for(int j=groupCounter;j<sizeSqr;j++){
                    if(elementsNumber[j]!=-1){
                        for(int k=0;k<elementsNumber[j];k++){
                            elements[i][k] = elements[j][k];
                            elements[j][k] = -1;
                        }
                        elementsNumber[i] = elementsNumber[j];
                        elementsNumber[j] = -1;
                    }
                }
            }
        }
    }
    public static void choose(int[][] elements,int[] elementsNumber,int group,int size,int[] link, int groupCounter,int sizeSqr){
        int counter = elementsNumber[group];
        int[] elementsOfGroup = new int[counter];
        if (counter >= 0) System.arraycopy(elements[group], 0, elementsOfGroup, 0, counter);
        int[] neighbours;
        boolean isContinue;
        int arCounter = 0;
        int[] ar = {0};
        do{
            isContinue = true;
            if(arCounter == counter ){
                break;
            }
            if(arCounter==0){
                ar = createUniqueNumbers(counter);
            }
            int index = ar[arCounter++];
            neighbours = encircle(size,elementsOfGroup[index]);
            int[] neighboursIndex;
            neighboursIndex = createUniqueNumbers(4);
            for(int i=0;i<4;i++){
                if(neighbours[neighboursIndex[i]]!=-1){
                    int secondGroupNumber = groupChecker(elements,neighbours[neighboursIndex[i]],groupCounter,sizeSqr);
                    if(secondGroupNumber!=group){
                        link[0] = elementsOfGroup[index];
                        link[1] = neighbours[neighboursIndex[i]];
                        link[2] = secondGroupNumber;
                        isContinue = false;
                        break;
                    }
                }
            }
        }while(isContinue);
    }
    public static void connect(char[][] maze, int pos1,int pos2,int group1,int group2,int[][] elements,int[] elementsNumber, int size){
        int mazePos1y =(pos1-1)/size+1;
        int mazePos1x = ((pos1-1)%size)+1;
        int mazePos2y =(pos2-1)/size+1;
        int mazePos2x = ((pos2-1)%size)+1;
        int xDistance = mazePos2x-mazePos1x;
        int yDistance = mazePos2y-mazePos1y;
        if(xDistance==0){
            if(yDistance == 1){
                maze[mazePos2y*2-2][mazePos1x*2-1] = ' ';
            }
            if(yDistance == -1){
                maze[mazePos2y*2][mazePos1x*2-1] = ' ';
            }
        }
        else{
            if(xDistance == 1){
                maze[mazePos1y*2-1][mazePos2x*2-2] = ' ';
            }
            if(xDistance == -1){
                maze[mazePos1y*2-1][mazePos2x*2] = ' ';
            }
        }
        int counter = elementsNumber[group1];
        for(int i = 0;i<elementsNumber[group2];i++){
            elements[group1][counter++] = elements[group2][i];
            elements[group2][i] = -1;
        }
        elementsNumber[group1] = counter;
        elementsNumber[group2] = -1;
    }
    //returns [0-(limit-1)]
    public static int[] createUniqueNumbers(int limit){
        ArrayList<Integer> list = new ArrayList<>();
        for (int i=0; i<limit; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        int[] ar = new int[limit];
        for(int i=0;i<limit;i++){
            ar[i] = list.get(i);
        }
        return ar;
    }
    public static int groupChecker(int[][] elements,int pos,int groupCounter,int sizeSqr){
        for(int i=0;i<groupCounter;i++){
            for(int j= 0;j<sizeSqr;j++){
                if(elements[i][j]==-1){
                    break;
                }
                if(elements[i][j]==pos){
                    return i;
                }
            }
        }
        return -1;
    }
    //takes [1-sizeSqr] -> returns [1-sizeSqr]
    public static int[] encircle(int size,int pos){
        int[] neighbours = new int[4];
        Arrays.fill(neighbours,-1);
        int i = (pos-1)/size;
        int j = (pos-1)%size;
        if(i == 0){
            if(j==0){
                neighbours[0] = 2;
                neighbours[1] = size+1;
            }
            else if(j==size-1){
                neighbours[0] = size*2;
                neighbours[1] = size-1;
            }
            else{
                neighbours[0] = pos+1;
                neighbours[1] = pos+size;
                neighbours[2] = pos -1;
            }
        }
        else if(j==0){
            if(i==size-1){
                neighbours[0] = pos+1;
                neighbours[1] = pos -size;
            }
            else{
                neighbours[0] = pos-size;
                neighbours[1] = pos+1;
                neighbours[2] = pos +size;
            }
        }
        else if(i==size-1){
            if(j==size-1){
                neighbours[0] = pos -size;
                neighbours[1] = pos -1;
            }
            else{
                neighbours[0] = pos -size;
                neighbours[1] = pos +1 ;
                neighbours[2] = pos-1;
            }
        }
        else if(j==size -1){
            neighbours[0] = pos -size;
            neighbours[1] = pos +size;
            neighbours[2] = pos -1;
        }
        else{
            neighbours[0] = pos -size;
            neighbours[1] = pos +1 ;
            neighbours[2] = pos + size;
            neighbours[3] = pos -1;
        }
        return neighbours;
    }
    public static void openEntrances(char[][] maze,int size){
        maze[0][1] = ' ';
        for(int i=2 * size-1 ;i>=0;i--){
            if(maze[2*size-1][i]==' '){
                maze[2*size][i] = ' ';
                break;
            }
        }
    }
    public static void printMaze(char[][] maze){
        System.out.println();
        for (char[] chars : maze) {
            for (int j = 0; j < maze.length; j++) {
                for (int k = 0; k < 2; k++) {
                    System.out.print(chars[j]);
                }
            }
            System.out.println();
        }
    }
}