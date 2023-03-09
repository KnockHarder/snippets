import csv


def write_csv(data: list[dict], path:str):
    if not writeFile:
        print(f'Write off: {path}')
        return
    if len(data) <= 0:
        print(f'No data to write: {path}')
        return
    with open(path, 'w') as f:
        f.write('\ufeff')
        csvWriter = csv.writer(f)
        first = data[0]
        headers = first.keys()
        csvWriter.writerow(headers)
        csvWriter.writerow([first.get(k) for k in headers])
        for i in range(1, len(data)):
            csvWriter.writerow([data[i].get(k) for k in headers])
        print(f'Wrote: {path}')
