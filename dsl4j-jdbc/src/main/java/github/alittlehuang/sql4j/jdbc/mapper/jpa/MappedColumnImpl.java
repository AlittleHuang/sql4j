package github.alittlehuang.sql4j.jdbc.mapper.jpa;

import github.alittlehuang.sql4j.jdbc.mapper.MappedColumn;
import jakarta.persistence.Column;

class MappedColumnImpl implements MappedColumn {

    private final Column column;

    static MappedColumnImpl wrap(Column column) {
        return column == null ? null : new MappedColumnImpl(column);
    }

    private MappedColumnImpl(Column column) {
        this.column = column;
    }

    @Override
    public boolean insertable() {
        return column.insertable();
    }

    @Override
    public boolean updatable() {
        return column.updatable();
    }
}
