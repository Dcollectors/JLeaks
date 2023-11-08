import pandas as pd
import os
import sys


if __name__ == '__main__':
    dataset_name = sys.argv[1]

    if dataset_name not in ['JLeaks', 'DroidLeaks']:
        print("Error: non-supported dataset") 
        sys.exit(0)

    input_path = '../dataset/{}/{}.csv'.format(dataset_name, dataset_name)

    output_path = '../dataset/{}/{}_addContent.csv'.format(dataset_name, dataset_name)

    funciton_contenct_path = '../dataset/{}/bug_method'.format(dataset_name)

    df = pd.read_csv(input_path)
    print(df)

    for home, dir, files in os.walk(funciton_contenct_path):
        for file in files:
            file_path = os.path.join(home, file)
            
            file_id = file.split('-')[1].strip('\ufeff')

            with open(file_path, 'r') as file_content:
                content = file_content.read()

            #print(file_id)
            mask = df['ID'] == int(file_id)
            
            if mask.any():
                df.loc[mask, 'file_content'] = content

    df.to_csv(output_path, index=False)
