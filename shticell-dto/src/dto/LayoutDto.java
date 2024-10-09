package dto;

import sheet.layout.api.LayoutGetters;

public class LayoutDto {
    public SizeDto size;
    public int rows;
    public int columns;

    public LayoutDto(LayoutGetters layout) {
        this.rows = layout.getRows();
        this.columns = layout.getColumns();
        this.size = new SizeDto(layout.getSize());
    }
}
