package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by Administrator on 2016/6/2.
 */
public class StockHistoryColumn {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull public static final String SYMBOL = "symbol";
    @DataType(DataType.Type.INTEGER) @Unique(onConflict = ConflictResolutionType.REPLACE) @NotNull public static final String DATE = "date";
    @DataType(DataType.Type.REAL) @NotNull public static final String OPEN = "open";
    @DataType(DataType.Type.REAL) @NotNull public static final String HIGH = "high";
    @DataType(DataType.Type.REAL) @NotNull public static final String LOW = "low";
    @DataType(DataType.Type.REAL) @NotNull public static final String CLOSE = "close";
    @DataType(DataType.Type.INTEGER) @NotNull public static final String VOLUME = "volume";
    @DataType(DataType.Type.REAL) @NotNull public static final String ADJ_CLOSE = "adj_close";
}
