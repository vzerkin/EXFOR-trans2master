
type EXFOR-2023-04-29.zip_* >EXFOR-2023-04-29.zip
type EXFOR-2023-05-15.zip_* >EXFOR-2023-05-15.zip
type EXFOR-2023-05-23.zip_* >EXFOR-2023-05-23.zip

dir EXFOR-2023-04-29.zip
dir EXFOR-2023-05-15.zip
dir EXFOR-2023-05-23.zip

unzip EXFOR-2023-04-29.zip EXFOR-2023-04-29.bck
unzip EXFOR-2023-05-15.zip EXFOR-2023-05-15.bck
unzip EXFOR-2023-05-23.zip EXFOR-2023-05-23.bck

@rem "C:\Program Files\7-Zip\7z.exe" x EXFOR-2023-05-23.zip EXFOR-2023-05-23.bck
