def cell_string_value(cell):
    value = cell.value
    if value is None:
        return ''
    return value.strip()


def cell_int_value(cell):
    v = cell.value
    if v is None:
        return 0
    if type(v) is int:
        return v
    if type(v) is float:
        return int(v)

    v = v.strip()
    if v.__len__() <= 0:
        return 0
    return int(v)


def cell_float_value(cell):
    v = cell.value
    if v is None:
        return 0
    if type(v) is float:
        return v
    v = v.strip()
    if v.__len__() <= 0:
        return 0
    return float(v.strip())
