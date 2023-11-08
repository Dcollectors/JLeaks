import jsonlines
from pygments.lexers import JavaLexer
from pygments.token import Token
import os

import os
import gzip

import argparse
import sys

def compress_file(s_file_path, t_file_path):
    with open(s_file_path, 'rb') as f_in:
        with gzip.open(t_file_path + '.gz', 'wb') as f_out:
            f_out.writelines(f_in)
    # os.remove(file_path)

def compress_folder(s_path, t_path):
    for root, dirs, files in os.walk(s_path):
        for file_name in files:
            s_file_path = os.path.join(root, file_name)
            t_file_path = os.path.join(t_path, file_name)
            compress_file(s_file_path, t_file_path)



if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Convert Java code to JSONL format.')

    parser.add_argument('--dataset', '-d', type=str, help='Choose the dataset (JLeaks or DroidLeaks).')
    parser.add_argument('--flag', '-f', type=str, help='Choose the flag (bug or all).')

    args = parser.parse_args()

    if not (args.dataset and args.flag):
        parser.error("Both --dataset and --flag must be provided")

    dataset = args.dataset
    flag = args.flag

    if dataset not in ['JLeaks', 'DroidLeaks']:
        parser.error("Invalid dataset. Supported values are 'JLeaks' or 'DroidLeaks'.")
    
    if flag not in ['bug', 'all']:
        parser.error("Invalid flag. Use 'bug' for bug methods or 'all' for fix and bug methods.")


    print('dataset:', args.dataset)
    print('flag:', args.flag)


    path = '../dataset/{}/{}_method'.format(dataset, flag)
    json_path = '../dataset/{}/{}_method_jsonl'.format(dataset, flag)
    gz_path = '../dataset/{}/{}_method_gz'.format(dataset, flag)

    if not os.path.exists(json_path):
        os.makedirs(json_path)
    
    if not os.path.exists(gz_path):
        os.makedirs(gz_path)

    if not os.path.exists(path):
        print(f"Error: Please check if the path '{path}' exists")
        sys.exit(1)

    index = 1
    for root, home, files in os.walk(path):
        for file in files:
            if not file.endswith("java"):
                continue
            
            file_path = os.path.join(root, file)
            with open(file_path, 'r', encoding='utf-8') as java_file:
                lines = java_file.readlines()
                if not lines:
                    break
                lines = ''.join(lines)

                lexer = JavaLexer()
                tokens = []
                for token in lexer.get_tokens(lines):
                    token_type = token[0]
                    token_value = token[1]
                    if token_type not in Token.Comment and token_value.strip():
                        tokens.append(token_value)

                result = {}
                result["filename"] = file_path
                result["tokens"] = tokens
                
                filename = file.split('.')[0] + ".jsonl"
                dest_path = os.path.join(json_path, filename)
                
                with jsonlines.open(dest_path, mode="w") as json_file:
                    json_file.write(result)
                index = index + 1

    compress_folder(json_path, gz_path)
