package github.alittlehuang.sql4j.jdbc.mapper.jpa;

import github.alittlehuang.sql4j.jdbc.mapper.MappedJoinColumn;
import jakarta.persistence.JoinColumn;

public class MappedJoinColumnImpl implements MappedJoinColumn {
    private final JoinColumn joinColumn;

    static MappedJoinColumnImpl wrap(JoinColumn joinColumn) {
        return joinColumn == null ? null : new MappedJoinColumnImpl(joinColumn);
    }

    private MappedJoinColumnImpl(JoinColumn joinColumn) {
        this.joinColumn = joinColumn;
    }

    @Override
    public String name() {
        return joinColumn.name();
    }

    @Override
    public String referencedColumnName() {
        return joinColumn.referencedColumnName();
    }

}
