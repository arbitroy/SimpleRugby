package com.simplyrugby.domain;

/**
 * Represents rugby positions available in the system.
 */
public class Position {
    // Constants for position names
    public static final String LOOSEHEAD_PROP = "Loose-head prop";
    public static final String HOOKER = "Hooker";
    public static final String TIGHTHEAD_PROP = "Tight-head prop";
    public static final String SECOND_ROW = "Second-row";
    public static final String BLINDSIDE_FLANKER = "Blindside flanker";
    public static final String OPENSIDE_FLANKER = "Open side flanker";
    public static final String NUMBER_8 = "Number 8";
    public static final String SCRUM_HALF = "Scrum-half";
    public static final String FLY_HALF = "Fly-half";
    public static final String INSIDE_CENTRE = "Inside centre";
    public static final String OUTSIDE_CENTRE = "Outside centre";
    public static final String RIGHT_WING = "Right wing";
    public static final String LEFT_WING = "Left wing";
    public static final String FULL_BACK = "Full-back";
    
    /**
     * Gets all available rugby positions
     * 
     * @return Array of position names
     */
    public static String[] getAllPositions() {
        return new String[] {
            LOOSEHEAD_PROP,
            HOOKER,
            TIGHTHEAD_PROP,
            SECOND_ROW,
            BLINDSIDE_FLANKER,
            OPENSIDE_FLANKER,
            NUMBER_8,
            SCRUM_HALF,
            FLY_HALF,
            INSIDE_CENTRE,
            OUTSIDE_CENTRE,
            RIGHT_WING,
            LEFT_WING,
            FULL_BACK
        };
    }
    
    /**
     * Gets the position for a given position number (1-15)
     * 
     * @param number The position number (1-15)
     * @return The corresponding position name, or null if invalid
     */
    public static String getPositionByNumber(int number) {
        switch (number) {
            case 1: return LOOSEHEAD_PROP;
            case 2: return HOOKER;
            case 3: return TIGHTHEAD_PROP;
            case 4: return SECOND_ROW;
            case 5: return SECOND_ROW;
            case 6: return BLINDSIDE_FLANKER;
            case 7: return OPENSIDE_FLANKER;
            case 8: return NUMBER_8;
            case 9: return SCRUM_HALF;
            case 10: return FLY_HALF;
            case 11: return INSIDE_CENTRE;
            case 12: return OUTSIDE_CENTRE;
            case 13: return RIGHT_WING;
            case 14: return LEFT_WING;
            case 15: return FULL_BACK;
            default: return null;
        }
    }
    
    /**
     * Gets the position number for a given position name
     * 
     * @param position The position name
     * @return The corresponding position number (1-15), or -1 if invalid
     */
    public static int getNumberByPosition(String position) {
        if (LOOSEHEAD_PROP.equals(position)) return 1;
        if (HOOKER.equals(position)) return 2;
        if (TIGHTHEAD_PROP.equals(position)) return 3;
        if (SECOND_ROW.equals(position)) return 4; // Note: Could also be 5
        if (BLINDSIDE_FLANKER.equals(position)) return 6;
        if (OPENSIDE_FLANKER.equals(position)) return 7;
        if (NUMBER_8.equals(position)) return 8;
        if (SCRUM_HALF.equals(position)) return 9;
        if (FLY_HALF.equals(position)) return 10;
        if (INSIDE_CENTRE.equals(position)) return 11;
        if (OUTSIDE_CENTRE.equals(position)) return 12;
        if (RIGHT_WING.equals(position)) return 13;
        if (LEFT_WING.equals(position)) return 14;
        if (FULL_BACK.equals(position)) return 15;
        return -1;
    }
    
    /**
     * Checks if the position is valid
     * 
     * @param position The position name to check
     * @return true if the position is valid, false otherwise
     */
    public static boolean isValidPosition(String position) {
        return getNumberByPosition(position) != -1;
    }
    
    /**
     * Gets the position group (forwards, backs) for a given position
     * 
     * @param position The position name
     * @return "Forwards" or "Backs", or null if invalid
     */
    public static String getPositionGroup(String position) {
        int number = getNumberByPosition(position);
        
        if (number >= 1 && number <= 8) {
            return "Forwards";
        } else if (number >= 9 && number <= 15) {
            return "Backs";
        } else {
            return null;
        }
    }
}