package com.company;

import java.util.Arrays;

//Didn't find the shortest path
public class PathFind {
    public static void findAPath(char[][] maze,int[] entryPoint){
        if(maze[entryPoint[0]][entryPoint[1]]!=' '){
            System.out.println("Wrong starting point");
            return;
        }
        int mazeSize = maze[0].length;
        int convertedMazeSize = (mazeSize-1)/2;
        iterate(maze,entryPoint,false,-1,mazeSize);
        int[] controlIfEntry = {entryPoint[0],entryPoint[1]};
        //howLongTheStringIs+step+y-x+availableDirections(top>right>bottom>left(0111))+howManyDirections+lastChoice[1-4\\-1]->16+0+15-7+1100+2+1
        String[] log = new String[(int)Math.pow(convertedMazeSize,2)-1];
        int logCounter = 0;
        int lastChoice = -2;
        while (true){
            String availableDirections;
            if(lastChoice!=-1){
                availableDirections = findAvailableDirections(maze,encircle(convertedMazeSize,convertToSizePos(entryPoint,convertedMazeSize)),convertedMazeSize);
                log[logCounter] = convertToLog(logCounter,entryPoint,availableDirections,lastChoice);
            }
            //Character.getNumericValue(log[logCounter].charAt(Integer.parseInt(log[logCounter].substring(0,2))-3))==1&&!controlLastMove(log[logCounter],lastChoice)
            else{
                logCounter = searchLog(log,logCounter);
                if(logCounter==-1){
                    System.out.println("There is no possible solution for this maze");
                    return;
                }
                else{
                    lastChoice = updateLastChoice(log[logCounter]);
                    int chosenPath = chooseAPath(extractAvailablePos(log[logCounter]),lastChoice);
                    lastChoice = chosenPath;
                    entryPoint = updateCurrentPos(log[logCounter],chosenPath);
                    logCounter++;
                    continue;
                }
            }
            if(control(maze,entryPoint,mazeSize)&&!Arrays.equals(extractPos(log,logCounter),controlIfEntry)){
                drawRoute(maze,log,logCounter);
                break;
            }
            //between 1,4
            int chosenPath = chooseAPath(availableDirections,lastChoice);
            lastChoice = chosenPath;
            if(chosenPath!=-1){
                entryPoint = updateCurrentPos(log[logCounter],chosenPath);
                logCounter++;
            }
        }
        iterate(maze,entryPoint,true,lastChoice,mazeSize);
    }
    public static String extractAvailablePos(String log){
        int plusCounter = 0;
        int i;
        for(i=log.length()-1;i>=0;i--){
            if (log.charAt(i)=='+') {
                plusCounter++;
                if(plusCounter==3){
                    break;
                }
            }
        }
        return log.substring(i+1,i+5);
    }
    public static void iterate(char[][] maze,int[] entryPoint,boolean isBroken,int lastChoice,int mazeSize){
        int[] neighbours = encircleNo2(maze,entryPoint,mazeSize);
        if(isBroken){
            int invalidMove =-1;
            switch (lastChoice){
                case 1:
                    invalidMove = 3;
                    break;
                case 2:
                    invalidMove = 4;
                    break;
                case 3:
                    invalidMove = 1;
                    break;
                case 4:
                    invalidMove = 2;
                    break;
            }
            for(int i=0;i<4;i++){
                if(i!=invalidMove-1&&neighbours[i]!=-1){
                    switch (i){
                        case 0:
                            entryPoint[0]++;
                            break;
                        case 1:
                            entryPoint[1]++;
                            break;
                        case 2:
                            entryPoint[0]--;
                            break;
                        case 3:
                            entryPoint[1]--;
                            break;
                    }
                    maze[entryPoint[0]][entryPoint[1]] = '/';
                    return;
                }
            }
        }
        else {
            maze[entryPoint[0]][entryPoint[1]] = '/';
            for(int i=0;i<4;i++){
                if(neighbours[i]!=-1){
                    switch (i){
                        case 0:
                            entryPoint[0]++;
                            break;
                        case 1:
                            entryPoint[1]++;
                            break;
                        case 2:
                            entryPoint[0]--;
                            break;
                        case 3:
                            entryPoint[1]--;
                            break;
                    }
                    return;
                }
            }
        }
    }
    public static int[] encircleNo2(char[][] maze,int[] entryPoint,int matrixSize){
        int[] neighbours = new int[4];
        Arrays.fill(neighbours,-1);
        if(entryPoint[0]>=1&&entryPoint[0]<=matrixSize-1){
            if(maze[entryPoint[0]-1][entryPoint[1]]==' '){
                neighbours[2] = 1;
            }
            if(maze[entryPoint[0]+1][entryPoint[1]] ==' '){
                neighbours[0] = 1;
            }
        }
        if(entryPoint[0]>=1){
            if(maze[entryPoint[0]-1][entryPoint[1]]==' '){
                neighbours[2] = 1;
            }
        }
        if(entryPoint[0]<=matrixSize-1){
            if(maze[entryPoint[0]+1][entryPoint[1]] ==' '){
                neighbours[0] = 1;
            }
        }
        if(entryPoint[1]>=1&&entryPoint[1]<=matrixSize-1){
            if(maze[entryPoint[0]][entryPoint[1]-1] == ' '){
                neighbours[3] = 1;
            }
            if(maze[entryPoint[0]][entryPoint[1]+1]==' '){
                neighbours[1] = 1;
            }
        }
        if(entryPoint[1]>=1){
            if(maze[entryPoint[0]][entryPoint[1]-1] == ' '){
                neighbours[3] = 1;
            }
        }
        if(entryPoint[1]<=matrixSize-1){
            if(maze[entryPoint[0]][entryPoint[1]+1]==' '){
                neighbours[1] = 1;
            }
        }
        return neighbours;
    }
    public static int chooseAPath(String availableDirections,int lastChoice){
        int invalid = -1;
        switch (lastChoice){
            case 1:
                invalid = 3;
                break;
            case 2:
                invalid = 4;
                break;
            case 3:
                invalid = 1;
                break;
            case 4:
                invalid = 2;
                break;
        }
        for(int i=0;i<4;i++){
            if(availableDirections.charAt(i)=='1'&&i!=invalid-1){
                return i+1;
            }
        }
        return -1;
    }
    public static int[] updateCurrentPos(String log,int chosenPath){
        int counter = 0;
        int plusCounter = 0;
        int index1 = 0;
        int index2;
        boolean control1 = true;
        while (true) {
            if(log.charAt(counter)=='+'){
                plusCounter++;
                if(plusCounter==2&&control1){
                    index1 = counter+1;
                    control1 = false;
                }
                else if(plusCounter==3){
                    index2 = counter;
                    break;
                }
            }
            counter++;
        }
        String posRaw = log.substring(index1,index2);
        int[] pos = new int[2];
        pos[0] =Integer.parseInt(posRaw.substring(0,posRaw.indexOf('-')));
        pos[1] = Integer.parseInt(posRaw.substring(posRaw.indexOf('-')+1));
        if(chosenPath==1){
            pos[0]-=2;
        }
        else if(chosenPath==2){
            pos[1]+=2;
        }
        else if(chosenPath==3){
            pos[0]+=2;
        }
        else {
            pos[1]-=2;
        }
        return pos;
    }
    public static int updateLastChoice(String log){
        return Character.getNumericValue(log.charAt(Integer.parseInt(log.substring(0,log.indexOf('+')))-1));
    }
    public static int searchLog(String[] log,int logCounter){
        for(int i=logCounter-1;i>=0;i--){
            if(extractPathNumber(log[i])>1){
                log[i] = decrementPathNumberAndClosePath(log[i],Character.getNumericValue(log[i+1].charAt(Integer.parseInt(log[i+1].substring(0,log[i+1].indexOf('+')))-1)));
                return i;
            }
        }
        return -1;
    }
    public static int extractPathNumber(String log){
        char[] logC = log.toCharArray();
        int plusCounter = 0;
        int i;
        for(i=logC.length-1;i>=0;i--){
            if (logC[i]=='+') {
                plusCounter++;
                if(plusCounter==1){
                    break;
                }
            }
        }
        return Character.getNumericValue(logC[i-1]);
    }
    public static String decrementPathNumberAndClosePath(String log,int nextLastChoice){
        char[] logC = log.toCharArray();
        int plusCounter = 0;
        int index1 = 0;
        int index2 = 0;
        boolean control1 = true;
        for(int i=logC.length-1;i>=0;i--){
            if (logC[i]=='+') {
                plusCounter++;
                if(plusCounter==1&&control1){
                    control1= false;
                    index1 = i;
                }
                if(plusCounter==3){
                    index2 = i;
                    break;
                }
            }
        }
        logC[index1-1] = (char)((Character.getNumericValue(logC[index1-1])-1)+'0');
        logC[index2+nextLastChoice] = '0';
        return String.valueOf(logC);
    }
    public static String convertToLog(int counter,int[] pos,String availableDirections,int lastChoice){
        String logTemp = counter+"+"+pos[0]+"-"+pos[1]+"+"+availableDirections+"+"+howManyChoices(availableDirections)+"+"+lastChoice;
        return (logTemp.length()+3)+"+"+counter+"+"+pos[0]+"-"+pos[1]+"+"+availableDirections+"+"+howManyChoices(availableDirections)+"+"+lastChoice;
    }
    public static int howManyChoices(String availableDirections){
        int counter = 0;
        for(int i=0;i<4;i++){
            if(availableDirections.charAt(i)=='1'){
                counter++;
            }
        }
        return counter;
    }
    public static String findAvailableDirections(char[][]maze,int[] neighbours,int mazeSize){
        int[] pos;
        StringBuilder availableDirections = new StringBuilder();
        for(int i=0;i<4;i++){
            if(neighbours[i]!=-1){
                pos = convertToMazePos(neighbours[i],mazeSize);
                switch (i){
                    case 0:
                        if(maze[pos[0]+1][pos[1]] ==' '){
                            availableDirections.append(1);
                        }
                        else{
                            availableDirections.append(0);
                        }
                        break;
                    case 1:
                        if(maze[pos[0]][pos[1]-1] ==' '){
                            availableDirections.append(1);
                        }
                        else{
                            availableDirections.append(0);
                        }
                        break;
                    case 2:
                        if(maze[pos[0]-1][pos[1]] ==' '){
                            availableDirections.append(1);
                        }
                        else{
                            availableDirections.append(0);
                        }
                        break;
                    case 3:
                        if(maze[pos[0]][pos[1]+1] ==' '){
                            availableDirections.append(1);
                        }
                        else{
                            availableDirections.append(0);
                        }
                        break;
                }
            }
            else{
                availableDirections.append(0);
            }
        }
        return availableDirections.toString();
    }
    public static int[] convertToMazePos(int sizePos,int mazeSize){
        int[] convertedPos = new int[2];
        convertedPos[0] = ((sizePos-1)/mazeSize)*2+1;
        convertedPos[1] = (((sizePos-1)%mazeSize))*2+1;
        return convertedPos;
    }
    public static int convertToSizePos(int[] entryPoint,int convertedMazeSize){
        return (entryPoint[1]-1)/2+1 +((entryPoint[0]-1)/2)*convertedMazeSize;
    }
    public static boolean control(char[][] maze,int[] entryPoint,int mazeSize){
        return (entryPoint[0] == mazeSize - 2 && maze[entryPoint[0] + 1][entryPoint[1]] == ' ') || (entryPoint[0] == 1 && maze[entryPoint[0] - 1][entryPoint[1]] == ' ') || (entryPoint[1] == mazeSize - 2 && maze[entryPoint[0]][entryPoint[1] + 1] == ' ') || (entryPoint[1] == 1 && maze[entryPoint[0]][entryPoint[1] - 1] == ' ');
    }
    public static int[] encircle(int mazeSize,int pos){
        int[] neighbours = new int[4];
        int i = (pos-1)/mazeSize;
        int j = (pos-1)%mazeSize;
        if(i == 0){
            if(j==0){
                neighbours[0] = -1;
                neighbours[1] = 2;
                neighbours[2] = mazeSize+1;
                neighbours[3] = -1;
            }
            else if(j==mazeSize-1){
                neighbours[0] = -1;
                neighbours[1] = -1;
                neighbours[2] = mazeSize*2;
                neighbours[3] = mazeSize-1;
            }
            else{
                neighbours[0] = -1;
                neighbours[1] = pos+1;
                neighbours[2] = pos+mazeSize;
                neighbours[3] = pos -1;
            }
        }
        else if(j==0){
            if(i==mazeSize-1){
                neighbours[0] = pos -mazeSize;
                neighbours[1] = pos+1;
                neighbours[2] = -1;
                neighbours[3] = -1;
            }
            else{
                neighbours[0] = pos-mazeSize;
                neighbours[1] = pos+1;
                neighbours[2] = pos +mazeSize;
                neighbours[3] = -1;
            }
        }
        else if(i==mazeSize-1){
            if(j==mazeSize-1){
                neighbours[0] = pos -mazeSize;
                neighbours[1] = -1;
                neighbours[2] = -1;
                neighbours[3] = pos -1;
            }
            else{
                neighbours[0] = pos -mazeSize;
                neighbours[1] = pos +1 ;
                neighbours[2] = -1;
                neighbours[3] = pos-1;
            }
        }
        else if(j==mazeSize -1){
            neighbours[0] = pos -mazeSize;
            neighbours[1] = -1;
            neighbours[2] = pos +mazeSize;
            neighbours[3] = pos -1;
        }
        else{
            neighbours[0] = pos -mazeSize;
            neighbours[1] = pos +1 ;
            neighbours[2] = pos + mazeSize;
            neighbours[3] = pos -1;
        }
        return neighbours;
    }
    public static void drawRoute(char[][] maze,String[] log,int logCounter){
        int[] pos;
        pos = extractPos(log,logCounter);
        for(int i=logCounter;i>0;i--){
            int lastMove = Character.getNumericValue(log[i].charAt(Integer.parseInt(log[i].substring(0,2))-1));
            switch (lastMove){
                case 1:
                    for(int j=0;j<2;j++){
                        maze[pos[0]][pos[1]] = '/';
                        pos[0]++;
                    }
                    break;
                case 2:
                    for(int j=0;j<2;j++){
                        maze[pos[0]][pos[1]] = '/';
                        pos[1]--;
                    }
                    break;
                case 3:
                    for(int j=0;j<2;j++){
                        maze[pos[0]][pos[1]] = '/';
                        pos[0]--;
                    }
                    break;
                case 4:
                    for(int j=0;j<2;j++){
                        maze[pos[0]][pos[1]] = '/';
                        pos[1]++;
                    }
                    break;
            }
        }
        maze[pos[0]][pos[1]] = '/';
    }
    public static int[] extractPos(String[] log,int logCounter){
        String posRaw = log[logCounter].substring(log[logCounter].indexOf('+',
                log[logCounter].indexOf('+')+1)+1,log[logCounter].indexOf('+',log[logCounter].indexOf('+',log[logCounter].indexOf('+')+1)+1));
        int[] pos = new int[2];
        pos[0] = Integer.parseInt(posRaw.substring(0,posRaw.indexOf('-')));
        pos[1] = Integer.parseInt(posRaw.substring(posRaw.indexOf('-')+1));
        return pos;
    }
}

