import argparse

def main(dataset, flag):
    input_path = "../dataset/{}/{}_method.log".format(dataset, flag)

    out_path = "../dataset/{}/{}_method_clean.log".format(dataset, flag)

    dupdicate_set_uniquess = set()
    dupdicate_set_consistency = set()


    with open(input_path,"r") as input_file:
        report_content = input_file.readlines()
        
        with open(out_path,"w") as output_file:
            for i in range(len(report_content)-8):
                temp=report_content[i + 2].replace(")","(")
                tempout=temp.split("(")
                file_in_Near_duplicate=tempout[1].split("-->")
                output_file.write(file_in_Near_duplicate[0]+"\n"+file_in_Near_duplicate[1]+"\n")
                file_in_Near_duplicate_0=file_in_Near_duplicate[0].split("/")
                file_in_Near_duplicate_1=file_in_Near_duplicate[1].split("/")

                if file_in_Near_duplicate_0[-1][0:3] == file_in_Near_duplicate_1[-1][0:3]:
                    output_file.write("2 files in the Near duplicate are the same. \n"+"\n\n\n")
                    dupdicate_set_uniquess.add(file_in_Near_duplicate_0[-1])
                    dupdicate_set_uniquess.add(file_in_Near_duplicate_1[-1])
                    
                else: 
                    output_file.write("2 files in the Near duplicate are the diffrent.\n"+"\n\n\n")
                    if file_in_Near_duplicate_0[-1][0:3] == 'bug':
                        dupdicate_set_consistency.add(file_in_Near_duplicate_0[-1])
                    if file_in_Near_duplicate_1[-1][0:3] == 'bug':
                        dupdicate_set_consistency.add(file_in_Near_duplicate_1[-1])
                    # dupdicate_consistency = dupdicate_consistency + 1
                    
            # output_file.write(f"Number of valid Near duplicate:{count}")

    all_count = 1094 if dataset == 'JLeaks' else 276

    if flag == "uniqueness":
        count = len(dupdicate_set_uniquess) 

        uniqueness = (all_count - count) / all_count
        formatted_uniqueness = "{:.3%}".format(uniqueness)
        print("Uniqueness: ", formatted_uniqueness)
    
    elif flag == "all":
        count = len(dupdicate_set_consistency) 
        uniqueness = (all_count - count) / all_count
        formatted_uniqueness = "{:.3%}".format(uniqueness)
        print("Consistency: ", formatted_uniqueness)



if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Compute uniqueness')

    parser.add_argument('--dataset', '-d', type=str, help='Choose the dataset (JLeaks or DroidLeaks).')
    parser.add_argument('--flag', '-f', type=str, help='Choose uniqueness or consistency.')
    
    args = parser.parse_args()

    if not (args.dataset and args.flag):
        parser.error("Both --dataset and --flag must be provided")

    dataset = args.dataset
    flag = args.flag

    if dataset not in ['JLeaks', 'DroidLeaks']:
        parser.error("Invalid dataset. Supported values are 'JLeaks' or 'DroidLeaks'.")

    if flag not in ['uniqueness', 'consistency']:
        parser.error("Invalid flag. Supported values are 'uniqueness' or 'consistency' ")

    print('dataset: ', dataset)
    print('item: ', flag)

    if flag == 'consistency':
        flag = 'all'

    main(dataset, flag)



