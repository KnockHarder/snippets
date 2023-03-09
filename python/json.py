import json

def write_json(data, path: str):
    if not writeFile:
        print(f'Write off: {path}')
        return
    with open(path, 'w') as f:
        f.write(json.dumps(data, ensure_ascii=False, indent=2))
        print(f'Wrote: {path}')
